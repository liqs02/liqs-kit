package com.patryklikus.kit.time

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals

class TimeTest {

    @AfterEach
    fun tearDown() = Time.resetClock()

    @Test
    fun `uses overridden clock for instantNow`() {
        val fixed = Instant.parse("2024-06-15T10:00:00Z")
        Time.setClock(Clock.fixed(fixed, ZoneOffset.UTC))

        assertEquals(fixed, Time.instantNow())
    }

    @Test
    fun `uses overridden clock for dateNow`() {
        Time.setClock(Clock.fixed(Instant.parse("2024-06-15T10:00:00Z"), ZoneOffset.UTC))

        assertEquals(LocalDate.of(2024, 6, 15), Time.dateNow())
    }

    @Test
    fun `uses overridden clock for dateTimeNow`() {
        Time.setClock(Clock.fixed(Instant.parse("2024-06-15T10:00:00Z"), ZoneOffset.UTC))

        assertEquals(LocalDateTime.of(2024, 6, 15, 10, 0), Time.dateTimeNow())
    }
}
