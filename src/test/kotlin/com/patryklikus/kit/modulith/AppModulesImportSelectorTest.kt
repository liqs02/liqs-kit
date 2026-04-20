package com.patryklikus.kit.modulith

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.core.type.AnnotationMetadata
import kotlin.test.assertEquals

class AppModulesImportSelectorTest {
    private val selector = AppModulesImportSelector()

    @Nested
    inner class SelectImports {

        @Test
        fun `imports all configs when every flag enabled`() {
            val imports = selector.selectImports(metadataOf<AllEnabled>())
            assertEquals(
                listOf(ModuleRoutingConfig::class.java.name, ModuleSchemaConfig::class.java.name),
                imports.toList(),
            )
        }

        @Test
        fun `skips routing when disabled`() {
            val imports = selector.selectImports(metadataOf<RoutingDisabled>())
            assertEquals(listOf(ModuleSchemaConfig::class.java.name), imports.toList())
        }

        @Test
        fun `skips schemas when disabled`() {
            val imports = selector.selectImports(metadataOf<SchemasDisabled>())
            assertEquals(listOf(ModuleRoutingConfig::class.java.name), imports.toList())
        }

        @Test
        fun `imports nothing when all disabled`() {
            val imports = selector.selectImports(metadataOf<AllDisabled>())
            assertEquals(0, imports.size)
        }
    }

    private inline fun <reified T : Any> metadataOf(): AnnotationMetadata =
        AnnotationMetadata.introspect(T::class.java)
}

@EnableAppModules
private class AllEnabled

@EnableAppModules(routing = false)
private class RoutingDisabled

@EnableAppModules(schemas = false)
private class SchemasDisabled

@EnableAppModules(routing = false, schemas = false)
private class AllDisabled
