package com.dgsw.lessflow.service.internal

import com.dgsw.lessflow.domain.vo.DetailedArticle

interface ScrapService {
    fun getArticlesWithNaver(keyword: String): List<DetailedArticle>

    fun getThumbnailUrl(keyword: String): String
}