package com.dgsw.lessflow.service.internal

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

interface TTSService {
    fun getSpeechPath(sentences: List<String>): String
}