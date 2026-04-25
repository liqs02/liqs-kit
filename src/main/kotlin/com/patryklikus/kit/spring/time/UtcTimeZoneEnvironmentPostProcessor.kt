package com.patryklikus.kit.spring.time

import org.springframework.boot.EnvironmentPostProcessor
import org.springframework.boot.SpringApplication
import org.springframework.core.env.ConfigurableEnvironment
import java.util.*

/**
 * Forces the JVM default time zone to UTC before any bean is created.
 *
 * Disable with `liqs.time.utc.enabled=false`.
 */
class UtcTimeZoneEnvironmentPostProcessor : EnvironmentPostProcessor {
    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        val enabled = environment.getProperty("liqs.time.utc.enabled", Boolean::class.java, true)
        if (enabled) {
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        }
    }
}
