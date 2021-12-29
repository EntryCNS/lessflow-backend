package com.dgsw.lessflow.service.internal

import com.dgsw.lessflow.domain.vo.*

interface ShorterService {
    fun shortArticle(article: String): ShortedArticle
}