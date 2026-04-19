package com.patryklikus.kit.modulith

import com.patryklikus.kit.testutil.Integration
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

@Integration
class ModuleRoutingConfigIT(
    private val client: WebTestClient
) {
    @Test
    fun `prefixes RestController with module id`() {
        client.get().uri("/sample/ping").exchange()
            .expectStatus().isOk
            .expectBody(String::class.java).isEqualTo("pong")
    }

    @Test
    fun `unprefixed path returns 404`() {
        client.get().uri("/ping").exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `ControllerAdvice still handles exceptions and is not prefixed`() {
        client.get().uri("/sample/boom").exchange()
            .expectStatus().isEqualTo(HttpStatus.I_AM_A_TEAPOT)
            .expectBody(String::class.java).isEqualTo("handled")
    }
}
