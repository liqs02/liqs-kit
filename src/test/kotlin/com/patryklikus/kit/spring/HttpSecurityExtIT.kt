package com.patryklikus.kit.spring

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.test.assertEquals

@SpringBootTest(classes = [HttpSecurityExtIT.TestSecurityApp::class])
@AutoConfigureMockMvc
class HttpSecurityExtIT {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Nested
    inner class RestModuleDefaults {

        @Test
        fun `denies access on matched path without authentication`() {
            assertEquals(403, mockMvc.perform(get("/api/v1/ping")).andReturn().response.status)
        }

        @Test
        fun `also matches second path pattern`() {
            assertEquals(403, mockMvc.perform(get("/api/v2/anything")).andReturn().response.status)
        }

        @Test
        fun `does not match unrelated path`() {
            assertEquals(404, mockMvc.perform(get("/other/ping")).andReturn().response.status)
        }
    }

    @Configuration
    @EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class])
    open class TestSecurityApp {
        @Bean
        open fun chain(http: HttpSecurity): SecurityFilterChain =
            http.restModuleDefaults("/api/v1/**", "/api/v2/**")

        @RestController
        open class PingController {
            @GetMapping("/api/v1/ping")
            fun ping() = "pong"
        }
    }
}
