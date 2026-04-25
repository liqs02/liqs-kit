package com.patryklikus.kit.spring.time

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication
import org.springframework.mock.env.MockEnvironment
import java.util.*
import kotlin.test.assertEquals

class UtcTimeZoneEnvironmentPostProcessorTest {

    private val processor = UtcTimeZoneEnvironmentPostProcessor()
    private val application = SpringApplication()
    private lateinit var originalTimeZone: TimeZone

    @BeforeEach
    fun saveTimeZone() {
        originalTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"))
    }

    @AfterEach
    fun restoreTimeZone() {
        TimeZone.setDefault(originalTimeZone)
    }

    @Nested
    inner class PostProcessEnvironment {
        @Test
        fun `sets default time zone to UTC by default`() {
            processor.postProcessEnvironment(MockEnvironment(), application)

            assertEquals(TimeZone.getTimeZone("UTC"), TimeZone.getDefault())
        }

        @Test
        fun `sets default time zone to UTC when explicitly enabled`() {
            val environment = MockEnvironment().withProperty("liqs.time.utc.enabled", "true")

            processor.postProcessEnvironment(environment, application)

            assertEquals(TimeZone.getTimeZone("UTC"), TimeZone.getDefault())
        }

        @Test
        fun `leaves default time zone untouched when disabled`() {
            val environment = MockEnvironment().withProperty("liqs.time.utc.enabled", "false")
            val before = TimeZone.getDefault()

            processor.postProcessEnvironment(environment, application)

            assertEquals(before, TimeZone.getDefault())
        }
    }
}
