package com.dgsw.lessflow.service

import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.io.File
import java.nio.file.attribute.BasicFileAttributes


@Service
class TTSServiceImpl : TTSService {
    override fun getSpeechPath(sentences: List<String>): String {
        val basePath = File("ttsTemp/${System.currentTimeMillis()}")
        basePath.mkdirs()

        val chromePrefs = HashMap<String, Any>()
        chromePrefs["profile.default_content_settings.popups"] = 0
        chromePrefs["download.default_directory"] = basePath.absolutePath

        val scraperOpt = ChromeOptions()
            .addArguments("--user-data-dir=/home/vinto/.config/chromium-crawl")
            .addArguments("--user-profile-dir=Crawler")
        scraperOpt.setExperimentalOption("prefs", chromePrefs)

        val scraper = ChromeDriver(scraperOpt)
        scraper.get("https://clovadubbing.naver.com/project/1608232")
        WebDriverWait(scraper, 3000).until { it.findElements(By.id("dubbing-add-textarea")).isNotEmpty() }

        val inputBox = scraper.findElementById("dubbing-add-textarea")
        val submitBox = scraper.findElement(By.cssSelector(".cursorPointer.btn.type_add"))

        sentences.forEach { sentence ->
            val sentenceId = sentences.indexOf(sentence) + 1
            inputBox.sendKeys(sentence)
            Thread.sleep(500)
            submitBox.click()
            Thread.sleep(1000)
            inputBox.clear()

            WebDriverWait(scraper, 15000).until { it.findElements(By.cssSelector(".btn_track_dubbing.cursorPointer")).isNotEmpty() }
            val dubbingElement = scraper.findElement(By.cssSelector(".btn_track_dubbing.cursorPointer"))
            dubbingElement.click()

            val downloadButton = scraper.findElement(By.cssSelector(".cursorPointer.btn.type_download.null"))
            val deleteButton = scraper.findElement(By.cssSelector(".cursorPointer.btn.type_delete.null"))

            Thread.sleep(1000)
            downloadButton.click()
            Thread.sleep(1000)
            deleteButton.click()
            Thread.sleep(500)
            val confirmDelete = scraper.findElements(By.cssSelector(".btn.cursorPointer"))
                .filter { it.text == "삭제" }.first()
            confirmDelete.click()
            Thread.sleep(1500)
        }

        scraper.close()

        // change name
        val audioList = basePath.listFiles().sortedByDescending { Files.readAttributes(it.toPath(), BasicFileAttributes::class.java).creationTime() }
        audioList.forEach { audio ->
            val id = audioList.indexOf(audio) + 1
            audio.renameTo(File("${basePath.absolutePath}/audio${id}.mp3"))
        }

        /*sentences.forEach { sentence ->
            val replaced = sentence.replace(" ", "+")
            val audio = File("${basePath.absolutePath}/${replaced}.mp3")
            println("${audio.absolutePath} is ${if(audio.exists()) "exists" else "no exists"}")
            audio.renameTo(File("audio${sentences.indexOf(sentence) + 1}.mp3"))
        }*/

        return basePath.absolutePath
    }
}