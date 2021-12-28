package com.dgsw.lessflow.utils

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL
import kr.co.shineware.nlp.komoran.core.Komoran
import kr.co.shineware.nlp.komoran.model.Token
import org.springframework.stereotype.Component

@Component
class NlpUtils {

    private val komoran = Komoran(DEFAULT_MODEL.FULL)

    private fun splitSentence(text: String): List<String> {
        val list = mutableListOf<String>()
        var quoteState = false

        var lastIndex = 0

        for(i in text.indices) {
            val c = text[i]
            if(!quoteState && c == '.') { // 인용문이 아닌 곳에 문장 분리자가 온다면 분리
                val splittedString = text.slice(lastIndex until i)
                list.add(splittedString)
                lastIndex = i + 1
            }
            if(c == '\'' || c == '"') quoteState = !quoteState
        }
        val last = text.slice(lastIndex until text.length)
        if(last.isNotBlank()) list.add(last)

        return list.toList()
    }

    // 전체 텍스트로부터 전처리된 문장들 반환
    fun getSentencesFromText(text: String): List<String> {
        val sentences = splitSentence(text).filter { it.isNotBlank() } // 문장으로 나누고, 빈 문장은 삭제
        return sentences.map {
            "${it.replace("\r\n", "").replace("\n", "")}." // 나눠지느라 사라진 점(.)을 다시 붙이고, 개행문자 제거
        }
    }

    // 문장이 명사 계열로 시작해서 조사 계열로 끝나는지 확인합니다
    private fun isSentenceStartWithNounEndsWithJosa(tokenList: List<Token>): Boolean {
        if(tokenList[0].pos != "NNP" && tokenList[0].pos != "NNG") return false

        for(i in tokenList.indices) {
            if(tokenList[i].pos.contains("NN")) continue
            if(tokenList[i].pos == "JKS" || tokenList[i].pos == "JX") return true
        }

        return false
    }

    // 제1 필터: 명사 계열로 시작해서 조사 계열로 끝나는 문장만 반환합니다
    fun filter1(sentenceList: List<String>): List<String> {
        return sentenceList.filter { sentence ->
            try { isSentenceStartWithNounEndsWithJosa(komoran.analyze(sentence).tokenList) }
                catch(ex: Exception) { false }
        }
    }

    private fun getNnpCount(sentence: String): Int {
        val tokens = komoran.analyze(sentence).tokenList
        return tokens.filter {  it.pos == "NNP" }.size
    }

    private fun getNnpFrequencyInSentence(sentenceList: List<String>): Int {
        val maxFrequency = 5

        var nnpCountForAverage = 0
        sentenceList.forEach { sentence ->
            nnpCountForAverage += getNnpCount(sentence)
        }
        val frequency = try {
            Math.min(nnpCountForAverage / sentenceList.size, maxFrequency) // 최댓값 못넘기게 설정
        } catch(ex: Exception) { maxFrequency / 2 }

        return frequency
    }

    // 제2 필터: 고유명사 비율이 평균치(+- 오차범위)안에 드는 문장만 반환합니다
    fun filter2(sentenceList: List<String>): List<String> {
        val threshold = 2.5
        val frequency = getNnpFrequencyInSentence(sentenceList)

        return sentenceList.filter { sentence ->
            val nnpCount = getNnpCount(sentence)
            IntRange(frequency - (frequency / threshold).toInt(),
                (frequency * 2.5).toInt()).contains(nnpCount)
        }
    }

}