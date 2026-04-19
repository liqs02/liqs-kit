package com.patryklikus.kit.testapp.sample

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties
data class SampleProperties(
    /** Greeting shown by the sample module. */
    val greeting: String = "default",
)

@ConfigurationProperties(prefix = "nested")
data class SampleNestedProperties(
    /** Arbitrary value used to verify sub-prefix binding. */
    val value: String = "default",
)
