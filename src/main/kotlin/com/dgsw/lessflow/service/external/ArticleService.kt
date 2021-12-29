package com.dgsw.lessflow.service.external

import com.dgsw.lessflow.domain.dto.request.CreateNewArticleDto
import com.dgsw.lessflow.domain.dto.response.ArticleInspectDto
import com.dgsw.lessflow.domain.dto.response.CreateNewArticleResponseDto
import com.dgsw.lessflow.domain.dto.response.ResponseDto
import com.dgsw.lessflow.entity.ArticleEntity

interface ArticleService {
    fun createNewArticle(createNewArticleDto: CreateNewArticleDto): CreateNewArticleResponseDto

    fun inspect(id: Long): ArticleInspectDto

    fun getVideoFile(id: Long): ByteArray
}