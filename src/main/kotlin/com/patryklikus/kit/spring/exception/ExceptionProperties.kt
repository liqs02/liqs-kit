package com.patryklikus.kit.spring.exception

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(ExceptionProperties.PREFIX)
data class ExceptionProperties(
    /** Enables the default mapping of application and Spring exceptions to JSON error responses. */
    val enabled: Boolean = true,
) {
    internal companion object {
        const val PREFIX = "liqs.exception"
    }
}
