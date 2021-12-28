package com.dgsw.lessflow

import com.dgsw.lessflow.service.ScrapService
import com.dgsw.lessflow.service.ShorterService
import com.dgsw.lessflow.service.VideoService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File

@SpringBootTest
class FullStepTest {
    @Autowired
    private lateinit var videoService: VideoService
    @Autowired
    private lateinit var scrapService: ScrapService

    @Test
    fun fullStep() {
        val myKeyword = "우크라이나"
        val articleList = scrapService.getArticlesWithNaver(myKeyword).slice(0..0) // 1개로 제한

        val testResultFile = File("remotion/src/newsData.ts")
        testResultFile.writeText(videoService.generateDataTypescript(articleList))
        videoService.buildVideoAndGetPath(articleList)
    }
}