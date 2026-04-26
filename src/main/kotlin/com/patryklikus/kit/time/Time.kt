package com.patryklikus.kit.time

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

/** Static time source. Defaults to UTC; tests override [clock] via reflection. */
object Time {
    private var clock: Clock = Clock.systemUTC()

    fun dateNow(): LocalDate = LocalDate.now(clock)
    fun dateTimeNow(): LocalDateTime = LocalDateTime.now(clock)
    fun instantNow(): Instant = Instant.now(clock)
}
