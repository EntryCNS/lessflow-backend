package com.dgsw.lessflow.service

import com.dgsw.lessflow.service.internal.ShorterService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.nio.charset.Charset

@SpringBootTest
class ShorterServiceTest {
    @Autowired
    private lateinit var shorterService: ShorterService

    @Test
    fun shortArticleTest() {
        val article1 = File("testcase/case1.txt").readText(Charset.forName("utf8"))
        val result = shorterService.shortArticle(article1)

        println("Original length: ${result.original.length} / Result length : ${result.result.length}")
        println(result.result)

        assertTrue(result.original.length > result.result.length)
    }
}