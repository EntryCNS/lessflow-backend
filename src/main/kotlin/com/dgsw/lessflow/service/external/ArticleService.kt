package com.dgsw.lessflow.service.external

import com.dgsw.lessflow.domain.dto.request.CreateNewArticleDto
import com.dgsw.lessflow.domain.dto.response.ArticleInspectDto
import com.dgsw.lessflow.domain.dto.response.CreateNewArticleResponseDto
import com.dgsw.lessflow.domain.dto.response.ResponseDto
import com.dgsw.lessflow.entity.ArticleEntity
import org.springframework.core.io.support.ResourceRegion
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestHeader

interface ArticleService {
    fun getAllAvailables(): List<ArticleInspectDto>

    fun createNewArticle(createNewArticleDto: CreateNewArticleDto): CreateNewArticleResponseDto

    fun inspect(id: Long): ArticleInspectDto

    fun getVideoFile(id: Long, header: HttpHeaders): ResponseEntity<ResourceRegion>
}