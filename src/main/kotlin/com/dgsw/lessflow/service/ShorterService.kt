package com.dgsw.lessflow.service

import com.dgsw.lessflow.domain.vo.*

interface ShorterService {
    fun shortArticle(article: String): ShortedArticle
}