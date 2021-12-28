package com.dgsw.lessflow.service

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

interface TTSService {
    fun getSpeechPath(sentences: List<String>): String
}