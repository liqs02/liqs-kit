package com.patryklikus.kit.testapp

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "outside")
data class OutsideProperties(
    /** Value of a property that lives outside any module's base package. */
    val value: String = "default",
)
