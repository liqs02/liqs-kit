package com.patryklikus.kit.spring

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus

var HttpServletResponse.httpStatus: HttpStatus
    get() = HttpStatus.valueOf(status)
    set(value) {
        status = value.value()
    }
