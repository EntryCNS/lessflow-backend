package com.dgsw.lessflow.controller

import com.dgsw.lessflow.domain.dto.request.CreateNewArticleDto
import com.dgsw.lessflow.domain.dto.response.ArticleInspectDto
import com.dgsw.lessflow.domain.dto.response.CreateNewArticleResponseDto
import com.dgsw.lessflow.domain.dto.response.ResponseDto
import com.dgsw.lessflow.service.external.ArticleService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.support.ResourceRegion
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("articles")
@Api(description= "Article 관련 API")
class ArticleController {
    @Autowired
    private lateinit var articleService: ArticleService

    @GetMapping
    @ApiOperation("사용 가능한 Article들을 가져옵니다")
    fun getAvailables(): ResponseDto<List<ArticleInspectDto>> {
        return ResponseDto(articleService.getAllAvailables())
    }

    @PostMapping()
    @ApiOperation("새 article 생성을 요청합니다")
    fun createNewArticle(@RequestBody createNewArticleDto: CreateNewArticleDto): ResponseDto<CreateNewArticleResponseDto> {
        return ResponseDto(articleService.createNewArticle(createNewArticleDto), 201, "Created")
    }

    @GetMapping("{id}")
    @ApiOperation("Article에 대한 정보를 가져옵니다")
    fun inspectArticle(@PathVariable id: Long): ResponseDto<ArticleInspectDto> {
        return ResponseDto(articleService.inspect(id))
    }

    @GetMapping("{id}/video", produces= arrayOf("video/mp4"))
    @ApiOperation("Article의 비디오를 가져옵니다")
    fun getVideoFile(@RequestHeader headers: HttpHeaders, @PathVariable id: Long): ResponseEntity<ResourceRegion> {
        return articleService.getVideoFile(id, headers)
    }

}