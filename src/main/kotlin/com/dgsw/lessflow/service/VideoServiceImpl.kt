package com.dgsw.lessflow.service

import com.dgsw.lessflow.domain.vo.DetailedArticle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

@Service
class VideoServiceImpl @Autowired constructor(
    private val ttsService: TTSService
): VideoService {

    private fun makeEscapeString(text: String): String {
        val escapeList = arrayOf<String>("'")
        var buf = ""

        text.forEach { ch ->
            buf += if(escapeList.contains(ch.toString())) { "\\${ch}" } else ch
        }

        return buf
    }

    override fun generateDataTypescript(newsList: List<DetailedArticle>): String {
        val newsStrings = mutableListOf<String>()
        newsList.forEach { news ->
            val originalSentences = news.processedArticle.resultSentenceList
            val sentences = originalSentences.map {
                "{ 'id': ${originalSentences.indexOf(it) + 1}, " +
                        "'content':'${makeEscapeString(it)}', " +
                        "'duration': ${0.168 * it.length - 1.5} " +
                        "}"
            }

            newsStrings.add(
                "{ " +
                        "'title': '${makeEscapeString(news.title)}', " +
                        "'provider': '${makeEscapeString(news.newsProvider)}', " +
                        "'backgroundImage': '${makeEscapeString(news.backgroundImageUrl)}', " +
                        "'sentences': [ ${sentences.joinToString(",")} ]}")
        }

        return "const newsData = [\n" +
                "    ${newsStrings.joinToString(",")}\n" +
                "]\n" +
                "\n" +
                "export default newsData"
    }

    override fun renderArticles(newsList: List<DetailedArticle>, tmpFolderId: Long) {
        val remotionDir = File("remotion/").absolutePath

        val tmpFolder = File("tmp/${tmpFolderId}")
        if(!tmpFolder.exists()) tmpFolder.mkdirs()

        var finishedJob = 0
        for(compositionId in 1..newsList.size) {
            val renderCommand = "cd ${remotionDir} && npx remotion render src/index.tsx article${compositionId} out/video${compositionId}.mp4"
            println(renderCommand)

            Thread {
                val runtime = Runtime.getRuntime().exec(arrayOf("/bin/sh", "-c", renderCommand))
                /*val reader = BufferedReader(InputStreamReader(runtime.inputStream))
                while(true) {
                    println(reader.readLine())
                }*/
                runtime.waitFor()

                val resultTarget = File("remotion/out/video${compositionId}.mp4")
                resultTarget.copyTo(File("${tmpFolder.absolutePath}/video${compositionId}.mp4"))
                resultTarget.delete()

                println("${compositionId}th render job finished.")
                finishedJob++
            }.start()
        }

        while(finishedJob != newsList.size) { Thread.sleep(10) }
    }

    override fun mergeAllVideo(tmpFolderId: Long) {
        val tmpFolder = File("tmp/${tmpFolderId}")
        if(!tmpFolder.exists()) tmpFolder.mkdirs()

        val videos = tmpFolder.listFiles { it -> it.isFile && it.extension == "mp4" }.sorted()
        val listText = videos.map { "file '${it.absolutePath}'" }.joinToString("\n")
        println(videos)
        println(listText)

        // save video list for ffmpeg
        File("${tmpFolder.absolutePath}/list.txt").writeText(listText)

        // execute merge command
        val mergeCommand = "cd ${tmpFolder.absolutePath} && ffmpeg -f concat -safe 0 -i list.txt -c copy merged.mp4"
        Runtime.getRuntime().exec(arrayOf("/bin/sh", "-c", mergeCommand)).waitFor()
    }

    override fun buildVideoAndGetPath(newsList: List<DetailedArticle>): String {
        val renderTmpId = System.currentTimeMillis()

        // get speech data
        val targetBaseFolder = File("remotion/audio").absoluteFile
        targetBaseFolder.mkdirs()
        targetBaseFolder.deleteRecursively()
        targetBaseFolder.mkdirs()

        newsList.forEach { news ->
            val audioFolder = File(ttsService.getSpeechPath(news.processedArticle.resultSentenceList))
            audioFolder.copyRecursively(File("${targetBaseFolder.absolutePath}/article${newsList.indexOf(news) + 1}"))
        }

        // render all article
        renderArticles(newsList, renderTmpId)

        // merge all video in temporary folder
        mergeAllVideo(renderTmpId)

        val tmpFolder = File("tmp/${renderTmpId}")
        return File("${tmpFolder.absolutePath}/merged.mp4").absolutePath
    }

}