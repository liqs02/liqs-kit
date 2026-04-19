package com.patryklikus.kit.modulith

import org.springframework.context.annotation.Import

/**
 * Enables per-module Spring Modulith conventions.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(AppModulesImportSelector::class)
annotation class EnableAppModules(
    /**
     * `/<moduleId>` path prefix for `@RestController`s in each module
     */
    val routing: Boolean = true,
    /**
     *  DB schema per module, derived from module id
     */
    val schemas: Boolean = true,
    /**
     * Prepend the module id to the prefix of `@ConfigurationProperties` classes in each module
     */
    val properties: Boolean = true,
)
