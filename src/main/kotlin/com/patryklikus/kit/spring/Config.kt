package com.patryklikus.kit.spring

import org.springframework.context.annotation.Configuration

/**
 * Indicates that a class declares one or more [org.springframework.context.annotation.Bean] methods and may be processed by the Spring container to generate bean definitions and service requests for those beans at runtime.
 *
 * @see Configuration
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Configuration(proxyBeanMethods = false)
annotation class Config
