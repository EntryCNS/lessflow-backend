package com.dgsw.lessflow.service.external

import com.dgsw.lessflow.domain.dto.request.CreateNewArticleDto
import com.dgsw.lessflow.domain.dto.response.ArticleInspectDto
import com.dgsw.lessflow.domain.dto.response.CreateNewArticleResponseDto
import com.dgsw.lessflow.domain.dto.response.ResponseDto
import com.dgsw.lessflow.domain.vo.ArticleStatus
import com.dgsw.lessflow.entity.ArticleEntity
import com.dgsw.lessflow.repository.ArticleRepository
import com.dgsw.lessflow.service.internal.ScrapService
import com.dgsw.lessflow.service.internal.VideoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpServerErrorException
import java.io.File

@Service
class ArticleServiceImpl: ArticleService {

    @Autowired
    private lateinit var videoService: VideoService
    @Autowired
    private lateinit var scrapService: ScrapService
    @Autowired
    private lateinit var articleRepository: ArticleRepository

    override fun createNewArticle(createNewArticleDto: CreateNewArticleDto): CreateNewArticleResponseDto {
        val keyword = createNewArticleDto.keyword
        val articleList = scrapService.getArticlesWithNaver(keyword)
        val articleLimitedList = articleList.slice(0 until Math.min(3, articleList.size)) // 최대 3개

        val newsDataTypeScript = File("remotion/src/newsData.ts")
        newsDataTypeScript.writeText(videoService.generateDataTypescript(articleLimitedList))
        val pathId = System.currentTimeMillis()

        val path = File("tmp/${pathId}/merged.mp4")
        val articleEntity = ArticleEntity.fromData(
            path.absolutePath,
            articleLimitedList.first().backgroundImageUrl,
            articleLimitedList.map { it.title }
        )

        val savedEntity = articleRepository.save(articleEntity)
        Thread {
            videoService.buildVideo(articleLimitedList, pathId)
            val entity = articleRepository.getById(savedEntity.id!!)
            entity.status = ArticleStatus.AVAILABLE
            articleRepository.save(entity)
        }.start()

        return CreateNewArticleResponseDto(savedEntity.id!!)
    }

    override fun inspect(id: Long): ArticleInspectDto {
        return ArticleInspectDto.fromEntity(articleRepository.findById(id).get())
    }

    override fun getVideoFile(id: Long): ByteArray {
        val rawInspected = articleRepository.findById(id).get()

        if(rawInspected.status == ArticleStatus.RENDERING)
            throw HttpServerErrorException(HttpStatus.PROCESSING, "video is rendering..")

        return File(rawInspected.mergedFilePath).readBytes()
    }
}