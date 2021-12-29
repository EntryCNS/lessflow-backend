package com.dgsw.lessflow.service

import com.dgsw.lessflow.service.internal.TTSService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TTSServiceTest {
    @Autowired
    private lateinit var ttsService: TTSService

    @Test
    fun ttsTest() {
        val sentences = listOf(
            "이런들 어떠하리 저런들 어떠하리",
            "만수산 드렁칡이 얽혀진들 어떠하리",
            "우리도 이같이 얽혀져 백 년까지 누리리라"
        )

        ttsService.getSpeechPath(sentences)
    }

}