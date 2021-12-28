package com.dgsw.lessflow.domain.vo

data class DetailedArticle(
    val title: String,
    val newsProvider: String,
    val backgroundImageUrl: String,
    val url: String,
    val processedArticle: ShortedArticle
)
