package com.patryklikus.kit.spring

import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.AnnotatedElementUtils
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class ConfigTest {
    @Test
    fun `is meta-annotated with @Configuration`() {
        assertNotNull(AnnotatedElementUtils.getMergedAnnotation(Config::class.java, Configuration::class.java))
    }

    @Test
    fun `bean methods are not proxied`() {
        val configuration = AnnotatedElementUtils.getMergedAnnotation(Config::class.java, Configuration::class.java)!!
        assertFalse(configuration.proxyBeanMethods)
    }

    @Test
    fun `class annotated with @Config is detected as @Configuration`() {
        assertNotNull(Sample::class.java.hasAnnotation<Configuration>())
    }
}

@Config
private class Sample
