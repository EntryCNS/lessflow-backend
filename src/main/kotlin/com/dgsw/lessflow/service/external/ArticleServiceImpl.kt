package com.dgsw.lessflow.service.external

import com.dgsw.lessflow.domain.dto.request.CreateNewArticleDto
import com.dgsw.lessflow.domain.dto.response.ArticleInspectDto
import com.dgsw.lessflow.domain.dto.response.CreateNewArticleResponseDto
import com.dgsw.lessflow.domain.vo.ArticleStatus
import com.dgsw.lessflow.entity.ArticleEntity
import com.dgsw.lessflow.repository.ArticleRepository
import com.dgsw.lessflow.service.internal.MailService
import com.dgsw.lessflow.service.internal.ScrapService
import com.dgsw.lessflow.service.internal.VideoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.UrlResource
import org.springframework.core.io.support.ResourceRegion
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpServerErrorException
import java.io.File
import java.util.*


@Service
class ArticleServiceImpl: ArticleService {

    @Autowired
    private lateinit var videoService: VideoService
    @Autowired
    private lateinit var scrapService: ScrapService
    @Autowired
    private lateinit var mailService: MailService
    @Autowired
    private lateinit var articleRepository: ArticleRepository

    override fun getAllAvailables(): List<ArticleInspectDto> {
        return articleRepository.getAllByStatusOrderById(ArticleStatus.AVAILABLE)
            .map { ArticleInspectDto.fromEntity(it) }
    }

    override fun createNewArticle(createNewArticleDto: CreateNewArticleDto): CreateNewArticleResponseDto {
        val keyword = createNewArticleDto.keyword
        val articleList = scrapService.getArticlesWithNaver(keyword)
        val articleLimitedList = articleList.slice(0 until Math.min(3, articleList.size)) // 최대 3개

        val newsDataTypeScript = File("remotion/src/newsData.ts")
        newsDataTypeScript.writeText(videoService.generateDataTypescript(articleLimitedList))
        val pathId = System.currentTimeMillis()

        val path = File("tmp/${pathId}/merged.mp4")
        val articleEntity = ArticleEntity.fromData(
            keyword,
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

            mailService.sendArticleMail(entity.id!!, createNewArticleDto.email)
        }.start()

        return CreateNewArticleResponseDto(savedEntity.id!!)
    }

    override fun inspect(id: Long): ArticleInspectDto {
        return ArticleInspectDto.fromEntity(articleRepository.findById(id).get())
    }

    override fun getVideoFile(id: Long, header: HttpHeaders): ResponseEntity<ResourceRegion> {

        val rawInspected = articleRepository.findById(id).get()

        if(rawInspected.status == ArticleStatus.RENDERING)
            throw HttpServerErrorException(HttpStatus.PROCESSING, "video is rendering..")

        val urlRes = UrlResource("file://${rawInspected.mergedFilePath}")

        val resourceRegion: ResourceRegion
        val chunkSize = 1000000L
        val contentLength: Long = urlRes.contentLength()
        val optional: Optional<HttpRange> = header.getRange().stream().findFirst()
        val httpRange: HttpRange

        if (optional.isPresent()) {
            httpRange = optional.get()
            val start = httpRange.getRangeStart(contentLength)
            val end = httpRange.getRangeEnd(contentLength)
            val rangeLength = java.lang.Long.min(chunkSize, end - start + 1)
            resourceRegion = ResourceRegion(urlRes, start, rangeLength)
        } else {
            val rangeLength = java.lang.Long.min(chunkSize, contentLength)
            resourceRegion = ResourceRegion(urlRes, 0, rangeLength)
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
            .contentType(MediaTypeFactory.getMediaType(urlRes).orElse(MediaType.APPLICATION_OCTET_STREAM))
            .body(resourceRegion)
    }
}