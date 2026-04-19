package com.patryklikus.kit.testapp.sample

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties
data class SampleProperties(val greeting: String = "default")

@ConfigurationProperties(prefix = "nested")
data class SampleNestedProperties(val value: String = "default")
