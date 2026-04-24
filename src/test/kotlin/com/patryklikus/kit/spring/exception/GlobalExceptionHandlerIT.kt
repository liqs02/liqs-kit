package com.patryklikus.kit.spring.exception

import com.patryklikus.kit.testapp.sample.TestItem
import com.patryklikus.kit.testutil.InternalIntegration
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@InternalIntegration
class GlobalExceptionHandlerIT(
    private val client: WebTestClient
) {
    @Nested
    inner class HandleApiError {
        @Test
        fun `translates NotFound to 404 with formatted message`() {
            client.get().uri("/sample/exception-test/not-found").exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.type").isEqualTo("NotFound")
                .jsonPath("$.message").isEqualTo("testitem not found")
        }

        @Test
        fun `translates Conflict to 409 with formatted message`() {
            client.get().uri("/sample/exception-test/conflict").exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody()
                .jsonPath("$.type").isEqualTo("Conflict")
                .jsonPath("$.message").isEqualTo("testitem with provided name already exists")
        }

        @Test
        fun `translates NoAssociation to 400 with formatted message`() {
            client.get().uri("/sample/exception-test/no-association").exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody()
                .jsonPath("$.type").isEqualTo("NoAssociation")
                .jsonPath("$.message").isEqualTo("Associated testitem not found")
        }

        @Test
        fun `translates InvalidData to 400 with given message`() {
            client.get().uri("/sample/exception-test/invalid-data").exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody()
                .jsonPath("$.type").isEqualTo("InvalidData")
                .jsonPath("$.message").isEqualTo("bad input")
        }

        @Test
        fun `translates IllegalState to 400 with given message`() {
            client.get().uri("/sample/exception-test/illegal-state").exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody()
                .jsonPath("$.type").isEqualTo("IllegalState")
                .jsonPath("$.message").isEqualTo("bad state")
        }

        @Test
        fun `translates Forbidden to 403 without message`() {
            client.get().uri("/sample/exception-test/forbidden").exchange()
                .expectStatus().isEqualTo(HttpStatus.FORBIDDEN)
                .expectBody()
                .jsonPath("$.type").isEqualTo("Forbidden")
                .jsonPath("$.message").doesNotExist()
        }

        @Test
        fun `translates Unauthorized to 401 without message`() {
            client.get().uri("/sample/exception-test/unauthorized").exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
                .expectBody()
                .jsonPath("$.type").isEqualTo("Unauthorized")
                .jsonPath("$.message").doesNotExist()
        }
    }

    @Nested
    inner class HandleMethodArgumentNotValid {
        @Test
        fun `returns 400 with first field error`() {
            client.post().uri("/sample/exception-test/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""{"name":""}""")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody()
                .jsonPath("$.type").isEqualTo("InvalidData")
                .jsonPath("$.message").exists()
        }
    }

    @Nested
    inner class HandleConstraintViolation {
        @Test
        fun `returns 400 with no message`() {
            client.get().uri("/sample/exception-test/constraint?value=a").exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody()
                .jsonPath("$.type").isEqualTo("InvalidData")
                .jsonPath("$.message").doesNotExist()
        }
    }

    @Nested
    inner class HandleHttpMessageNotReadable {
        @Test
        fun `returns 400 with generic message`() {
            client.post().uri("/sample/exception-test/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("not json")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody()
                .jsonPath("$.type").isEqualTo("InvalidData")
                .jsonPath("$.message").isEqualTo("Malformed request body")
        }
    }

    @Nested
    inner class HandleMethodArgumentTypeMismatch {
        @Test
        fun `returns 400 with parameter name message`() {
            client.get().uri("/sample/exception-test/type-mismatch/not-a-number").exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody()
                .jsonPath("$.type").isEqualTo("InvalidData")
                .jsonPath("$.message").isEqualTo("Invalid value for parameter 'id'")
        }
    }

    @Nested
    inner class HandleMissingPathVariable {
        @Test
        fun `returns 404 when parameter is @Entity annotated`() {
            client.get().uri("/sample/exception-test/missing-entity-path").exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody()
                .jsonPath("$.type").isEqualTo("NotFound")
                .jsonPath("$.message").isEqualTo("${TestItem::class.simpleName!!.lowercase()} not found")
        }

        @Test
        fun `returns 500 when parameter is not @Entity annotated`() {
            client.get().uri("/sample/exception-test/missing-plain-path").exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.type").isEqualTo("InternalServerError")
        }
    }

    @Nested
    inner class HandleAccessDenied {
        @Test
        fun `returns 403`() {
            client.get().uri("/sample/exception-test/access-denied").exchange()
                .expectStatus().isEqualTo(HttpStatus.FORBIDDEN)
                .expectBody()
                .jsonPath("$.type").isEqualTo("Forbidden")
        }
    }

    @Nested
    inner class HandleCustomApiErrorSubclass {
        @Test
        fun `translates external ApiError subclass using its type and message`() {
            client.get().uri("/sample/exception-test/custom-api-error").exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody()
                .jsonPath("$.type").isEqualTo("IllegalState")
                .jsonPath("$.message").isEqualTo("insufficient funds")
        }
    }

    @Nested
    inner class HandleErrorResponse {
        @Test
        fun `forwards ErrorResponseException status with empty body`() {
            client.get().uri("/sample/exception-test/error-response").exchange()
                .expectStatus().isEqualTo(HttpStatus.I_AM_A_TEAPOT)
                .expectBody().isEmpty
        }

        @Test
        fun `forwards NoResourceFoundException status with empty body`() {
            client.get().uri("/sample/definitely-not-a-route").exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody().isEmpty
        }
    }

    @Nested
    inner class HandleFallback {
        @Test
        fun `returns 500 for unhandled exceptions`() {
            client.get().uri("/sample/exception-test/unexpected").exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.type").isEqualTo("InternalServerError")
        }
    }
}
