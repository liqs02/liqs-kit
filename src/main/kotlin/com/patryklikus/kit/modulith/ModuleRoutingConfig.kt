package com.patryklikus.kit.modulith

import com.patryklikus.kit.spring.hasAnnotation
import org.springframework.modulith.core.ApplicationModules
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/** Applies a `/<moduleId>` path prefix to `@RestController`s in each module's base package. */
class ModuleRoutingConfig(private val modules: ApplicationModules) : WebMvcConfigurer {
    override fun configurePathMatch(configurer: PathMatchConfigurer) =
        modules.forEach { module ->
            val modulePackage = module.basePackage.name
            configurer.addPathPrefix("/${module.identifier}") { type ->
                type.`package`.name.startsWith(modulePackage) &&
                        type.hasAnnotation<RestController>()
            }
        }
}
