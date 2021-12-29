package com.dgsw.lessflow.entity

import com.dgsw.lessflow.domain.vo.ArticleStatus
import com.dgsw.lessflow.service.external.ArticleService
import javax.persistence.*

@Entity
data class ArticleEntity(

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var id: Long? = null,

    var mergedFilePath: String,

    val thumbnailUrl: String,

    val articleTitles: String,

    @Enumerated(EnumType.STRING)
    var status: ArticleStatus

) {
    companion object {
        fun fromData(mergedFilePath: String, thumbnailUrl: String, articleTitles: List<String>): ArticleEntity {
            return ArticleEntity(
                mergedFilePath= mergedFilePath,
                thumbnailUrl= thumbnailUrl,
                articleTitles= articleTitles.joinToString("|"),
                status= ArticleStatus.RENDERING
            )
        }
    }
}
