package com.dgsw.lessflow.service

import com.dgsw.lessflow.service.internal.MailService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MailServiceTest {
    @Autowired
    private lateinit var mailService: MailService

    @Test
    fun sendEmail() {
        mailService.sendArticleMail(3, "develretr0@dgsw.hs.kr")
    }
}