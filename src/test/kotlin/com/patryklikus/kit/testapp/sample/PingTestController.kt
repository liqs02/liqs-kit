package com.patryklikus.kit.testapp.sample

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PingTestController {
    @GetMapping("/ping")
    fun ping() = "pong"

    @GetMapping("/boom")
    fun boom(): Nothing = throw IllegalStateException("boom")
}

@ControllerAdvice
class BoomTestAdvice {
    @ExceptionHandler(IllegalStateException::class)
    fun handle(e: IllegalStateException): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("handled")
}
