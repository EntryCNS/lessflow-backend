package com.dgsw.lessflow.entity

import com.dgsw.lessflow.domain.vo.ArticleStatus
import com.dgsw.lessflow.service.external.ArticleService
import javax.persistence.*

@Entity
data class ArticleEntity(

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    var id: Long? = null,

    val keyword: String,

    var mergedFilePath: String,

    val thumbnailUrl: String,

    val articleTitles: String,

    @Enumerated(EnumType.STRING)
    var status: ArticleStatus

) {
    companion object {
        fun fromData(keyword:String, mergedFilePath: String, thumbnailUrl: String, articleTitles: List<String>): ArticleEntity {
            return ArticleEntity(
                keyword= keyword,
                mergedFilePath= mergedFilePath,
                thumbnailUrl= thumbnailUrl,
                articleTitles= articleTitles.joinToString("|"),
                status= ArticleStatus.RENDERING
            )
        }
    }
}
