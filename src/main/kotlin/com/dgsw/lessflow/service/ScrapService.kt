package com.dgsw.lessflow.service

import com.dgsw.lessflow.domain.vo.DetailedArticle

interface ScrapService {
    fun getArticlesWithNaver(keyword: String): List<DetailedArticle>
}