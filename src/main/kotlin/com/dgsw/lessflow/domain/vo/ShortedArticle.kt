package com.dgsw.lessflow.domain.vo

data class ShortedArticle(
    val original: String,
    val result: String,
    val resultSentenceList: List<String>
)
