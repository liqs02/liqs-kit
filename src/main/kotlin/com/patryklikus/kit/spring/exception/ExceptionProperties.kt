package com.patryklikus.kit.spring.exception

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(ExceptionProperties.PREFIX)
data class ExceptionProperties(
    /** Whether [GlobalExceptionHandler] is registered. */
    val enabled: Boolean = true,
) {
    companion object {
        const val PREFIX = "liqs.exception"
    }
}
