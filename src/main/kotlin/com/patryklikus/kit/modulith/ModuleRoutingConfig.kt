package com.patryklikus.kit.modulith

import com.patryklikus.kit.spring.hasAnnotation
import org.springframework.modulith.core.ApplicationModule
import org.springframework.modulith.core.ApplicationModules
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Applies a `/<moduleId>` path prefix to `@RestController`s in each module's base package.
 *
 * Nested module ids translate dots to URL segments (`location.address` → `/location/address`),
 * and each controller is prefixed by its most specific module only.
 */
class ModuleRoutingConfig(private val modules: ApplicationModules) : WebMvcConfigurer {
    override fun configurePathMatch(configurer: PathMatchConfigurer) {
        val modulesBySpecificity = modules.sortedByDescending { it.basePackage.name.length }
        modules.forEach { module ->
            val pathPrefix = "/" + module.identifier.toString().replace('.', '/')
            configurer.addPathPrefix(pathPrefix) { type ->
                type.hasAnnotation<RestController>() && type.mostSpecificModule(modulesBySpecificity) == module
            }
        }
    }

    private fun Class<*>.mostSpecificModule(modulesBySpecificity: List<ApplicationModule>): ApplicationModule? =
        modulesBySpecificity.firstOrNull { `package`.name.isInPackage(it.basePackage.name) }

    private fun String.isInPackage(base: String): Boolean = this == base || startsWith("$base.")
}
