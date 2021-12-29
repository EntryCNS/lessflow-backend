package com.dgsw.lessflow.repository

import com.dgsw.lessflow.entity.ArticleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository: JpaRepository<ArticleEntity, Long> {

}