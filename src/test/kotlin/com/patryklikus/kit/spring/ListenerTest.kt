package com.patryklikus.kit.spring

import org.junit.jupiter.api.Test
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.stereotype.Component
import kotlin.test.assertNotNull

class ListenerTest {
    @Test
    fun `is meta-annotated with @Component`() {
        assertNotNull(AnnotatedElementUtils.getMergedAnnotation(Listener::class.java, Component::class.java))
    }

    @Test
    fun `class annotated with @Listener is detected as @Component`() {
        assertNotNull(ListenerSample::class.java.hasAnnotation<Component>())
    }
}

@Listener
private class ListenerSample
