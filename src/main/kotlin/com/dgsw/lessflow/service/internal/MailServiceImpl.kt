package com.dgsw.lessflow.service.internal

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import javax.mail.*

import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.io.File
import java.util.*
import javax.mail.internet.MimeMessage

@Service
class MailServiceImpl @Autowired constructor(
    private val templateEngine: TemplateEngine
): MailService {

    @Value("\${email.authfile}")
    lateinit var authFile: String

    private class GmailAuthenticator(val gmailId: String, val gmailPassword: String): Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(gmailId, gmailPassword)
        }
    }

    override fun sendMail(title: String, body: String, to: String, isHtml: Boolean) {
        val mailProps = Properties()

        mailProps.put("mail.smtp.port", "465")
        mailProps.put("mail.smtp.ssl.enable", "true")
        mailProps.put("mail.smtp.starttls.enable", "true")

        // Setup SMTP server
        mailProps.put("mail.smtp.host", "smtp.gmail.com")
        mailProps.put("mail.smtp.socketFactory.fallback", "false")
        mailProps.put("mail.smtp.debug", "true")
        mailProps.put("mail.smtp.auth", "true")

        // Setup smtp authorizational data
        val mailSender = JavaMailSenderImpl()

        val authFileTokens = File(authFile).readText()
            .replace("\r\n", "")
            .replace("\r", "")
            .replace("\n", "")
            .split("|")

        val emailSender = authFileTokens[0]
        val emailPassword = authFileTokens[1]
        println(authFileTokens)

        val smtpAuth = GmailAuthenticator(emailSender.split("@").first(), emailPassword)
        val smtpSession = Session.getDefaultInstance(mailProps, smtpAuth)
        val mailMessage: MimeMessage = mailSender.createMimeMessage()
        val innerMessage: MimeMessageHelper = MimeMessageHelper(mailMessage, false, "UTF-8")
        innerMessage.setTo(to)
        innerMessage.setFrom("lessflow <$emailSender>")
        innerMessage.setSubject("[lessflow] $title")
        innerMessage.setText(body, isHtml)

        // Send mail
        mailSender.javaMailProperties = mailProps
        mailSender.session = smtpSession

        mailSender.send(mailMessage)
    }

    @Value("\${email.server-endpoint}")
    lateinit var serverEndpoint: String

    override fun sendArticleMail(articleId: Long, to: String) {
        val renderContext = Context()
        renderContext.setVariable("url", "$serverEndpoint/articles/$articleId/video")

        val renderedHtml = templateEngine.process("articleMailTemplate.html", renderContext)
        sendMail("따끈따끈한 새 소식이 도착했습니다!", renderedHtml, to)
    }
}