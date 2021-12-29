package com.dgsw.lessflow.service.internal

import com.dgsw.lessflow.domain.vo.*
import com.dgsw.lessflow.utils.NlpUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ShorterServiceImpl @Autowired constructor(
    private val nlpUtils: NlpUtils
): ShorterService {

    override fun shortArticle(article: String): ShortedArticle {
        val sentences = nlpUtils.getSentencesFromText(article)

        val filter1 = nlpUtils.filter1(sentences)
        val filter2 = nlpUtils.filter2(filter1)

        val resultString = filter2.joinToString("\n")
        return ShortedArticle(article, resultString, filter2)
    }

}