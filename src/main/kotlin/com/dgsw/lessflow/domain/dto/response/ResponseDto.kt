package com.dgsw.lessflow.domain.dto.response

data class ResponseDto<T>(val data: T? = null, val code: Int = 200, val message: String = "OK")
