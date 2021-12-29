package com.dgsw.lessflow.service.internal

interface MailService {
    fun sendMail(title: String, body: String, to: String, isHtml: Boolean = true)

    fun sendArticleMail(articleId: Long, to: String)
}
