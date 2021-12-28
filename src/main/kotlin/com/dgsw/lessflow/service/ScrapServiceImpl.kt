package com.dgsw.lessflow.service

import com.dgsw.lessflow.domain.vo.DetailedArticle
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.HashMap

@Service
class ScrapServiceImpl @Autowired constructor(
    private val shorterService: ShorterService
): ScrapService {
    init {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")
    }

    override fun getArticlesWithNaver(keyword: String): List<DetailedArticle> {
        val scraper = ChromeDriver()

        scraper.get("https://search.naver.com/search.naver?where=news&sm=tab_jum&query=$keyword")
        WebDriverWait(scraper, 3000).until { it.findElements(By.className("list_news")).isNotEmpty() }

        class PreDetail(val title: String, val provider: String, val backgroundImage: String)
        val newsMap: HashMap<PreDetail, String> = hashMapOf()

        scraper.findElement(By.className("list_news")).findElements(By.className("bx")).forEach { news ->
            val hrefList = news.findElements(By.cssSelector("a.info:not(.press)"))
            if(hrefList.size == 1) {
                val newsTitle = news.findElement(By.className("news_tit")).text
                val provider = news.findElement(By.cssSelector(".info.press")).text.replace("언론사 선정", "")
                val newsUrl = hrefList.first().getAttribute("href")
                val bgUrl = news.findElement(By.cssSelector(".thumb.api_get")).getAttribute("src").split("&type=ff").first()

                newsMap.set(PreDetail(newsTitle, provider, bgUrl), newsUrl)
            }
        }

        val newsList = mutableListOf<DetailedArticle>()

        newsMap.forEach { (preDetail, url) ->
            scraper.get(url)
            WebDriverWait(scraper, 3000).until { it.findElements(By.className("article_info")).isNotEmpty() }
            if(scraper.currentUrl.contains("entertain.naver.com")) return@forEach

            scraper.executeScript("const list = document.getElementsByClassName('img_desc'); for(let i = 0; i < list.length; i++) { list[i].textContent = '' }")

            val article = scraper.findElement(By.className("article_body_contents"))
            val articleText = article.text

            val result = shorterService.shortArticle(articleText)
            newsList.add(DetailedArticle(preDetail.title, preDetail.provider, preDetail.backgroundImage, url, result))
        }

        scraper.close()

        return newsList
    }

}