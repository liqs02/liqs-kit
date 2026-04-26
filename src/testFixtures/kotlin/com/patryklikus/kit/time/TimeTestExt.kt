package com.patryklikus.kit.time

import java.time.Clock

fun Time.setClock(clock: Clock) {
    val field = Time::class.java.getDeclaredField("clock").apply { isAccessible = true }
    field.set(Time, clock)
}

fun Time.resetClock() = setClock(Clock.systemUTC())
