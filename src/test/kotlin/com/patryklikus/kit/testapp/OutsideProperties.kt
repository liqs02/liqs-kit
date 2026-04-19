package com.patryklikus.kit.testapp

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "outside")
data class OutsideProperties(val value: String = "default")
