package com.patryklikus.kit.testapp.sample.nested

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NestedPingTestController {
    @GetMapping("/ping")
    fun ping() = "nested-pong"
}
