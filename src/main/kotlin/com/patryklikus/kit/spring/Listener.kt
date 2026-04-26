package com.patryklikus.kit.spring

import org.springframework.stereotype.Component

/** Marks a class whose methods react to application events (typically via `@ApplicationModuleListener`). */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class Listener
