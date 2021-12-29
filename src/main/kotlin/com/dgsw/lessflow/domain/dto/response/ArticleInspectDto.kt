package com.dgsw.lessflow.domain.dto.response

import com.dgsw.lessflow.domain.vo.ArticleStatus
import com.dgsw.lessflow.entity.ArticleEntity

data class ArticleInspectDto(
    val id: Long,
    val thumbnailUrl: String,
    val articleTitles: List<String>,
    val status: ArticleStatus
) {
    companion object {
        fun fromEntity(entity: ArticleEntity): ArticleInspectDto {
            return ArticleInspectDto(
                id= entity.id!!,
                thumbnailUrl= entity.thumbnailUrl,
                articleTitles= entity.articleTitles.split("|"),
                status= entity.status
            )
        }
    }
}
