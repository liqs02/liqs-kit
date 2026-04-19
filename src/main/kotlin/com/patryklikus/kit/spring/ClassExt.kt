package com.patryklikus.kit.spring

import org.springframework.core.annotation.AnnotatedElementUtils

/** Reified wrapper over `AnnotatedElementUtils.hasAnnotation`. */
inline fun <reified A : Annotation> Class<*>.hasAnnotation(): Boolean =
    AnnotatedElementUtils.hasAnnotation(this, A::class.java)
