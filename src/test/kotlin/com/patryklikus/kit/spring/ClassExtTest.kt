package com.patryklikus.kit.spring

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ClassExtTest {
    @Test
    fun `returns true when class is directly annotated`() {
        assertTrue(Annotated::class.java.hasAnnotation<Marker>())
    }

    @Test
    fun `returns false when class is not annotated`() {
        assertFalse(NotAnnotated::class.java.hasAnnotation<Marker>())
    }

    @Test
    fun `finds meta-annotation via AnnotatedElementUtils`() {
        assertTrue(MetaAnnotated::class.java.hasAnnotation<Marker>())
    }
}

private annotation class Marker

@Marker
private annotation class ComposedMarker

@Marker
private class Annotated

private class NotAnnotated

@ComposedMarker
private class MetaAnnotated
