package com.patryklikus.kit.spring.exception

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.ControllerAdvice

@AutoConfiguration
@ConditionalOnClass(ControllerAdvice::class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
    prefix = ExceptionProperties.PREFIX,
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
@EnableConfigurationProperties(ExceptionProperties::class)
class GlobalExceptionHandlerAutoConfiguration {
    @Bean
    fun globalExceptionHandler() = GlobalExceptionHandler()
}
