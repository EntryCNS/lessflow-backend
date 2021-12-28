package com.dgsw.lessflow.service

import com.dgsw.lessflow.domain.vo.DetailedArticle

interface VideoService {
    fun generateDataTypescript(newsList: List<DetailedArticle>): String

    fun renderArticles(newsList: List<DetailedArticle>, tmpFolderId: Long)

    fun mergeAllVideo(tmpFolderId: Long)

    fun buildVideoAndGetPath(newsList: List<DetailedArticle>): String
}