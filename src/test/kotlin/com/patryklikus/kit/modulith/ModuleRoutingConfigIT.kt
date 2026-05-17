package com.patryklikus.kit.modulith

import com.patryklikus.kit.testutil.InternalIntegration
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

@InternalIntegration
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
    fun `prefixes RestController in nested module with dotted id translated to path`() {
        client.get().uri("/sample/nested/ping").exchange()
            .expectStatus().isOk
            .expectBody(String::class.java).isEqualTo("nested-pong")
    }

    @Test
    fun `nested module controller is not also served under parent module prefix`() {
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
