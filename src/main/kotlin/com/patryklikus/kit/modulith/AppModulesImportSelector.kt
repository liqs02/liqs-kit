package com.patryklikus.kit.modulith

import org.springframework.context.annotation.ImportSelector
import org.springframework.core.type.AnnotationMetadata

internal class AppModulesImportSelector : ImportSelector {
    override fun selectImports(metadata: AnnotationMetadata): Array<String> {
        val attributes = metadata.getAnnotationAttributes(EnableAppModules::class.java.name)!!
        return buildList {
            if (attributes["routing"] as Boolean) add(ModuleRoutingConfig::class.java.name)
            if (attributes["schemas"] as Boolean) add(ModuleSchemaConfig::class.java.name)
            if (attributes["properties"] as Boolean) add(ModulePropertiesConfig::class.java.name)
        }.toTypedArray()
    }
}
