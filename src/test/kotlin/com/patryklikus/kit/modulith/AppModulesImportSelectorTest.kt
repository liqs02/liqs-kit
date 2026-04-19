package com.patryklikus.kit.modulith

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.core.type.AnnotationMetadata
import kotlin.test.assertContains
import kotlin.test.assertEquals

class AppModulesImportSelectorTest {
    private val selector = AppModulesImportSelector()

    @Nested
    inner class SelectImports {

        @Test
        fun `imports all configs when every flag enabled`() {
            val imports = selector.selectImports(metadataOf<AllEnabled>())
            assertEquals(3, imports.size)
            assertContains(imports.toList(), ModuleRoutingConfig::class.java.name)
            assertContains(imports.toList(), ModuleSchemaConfig::class.java.name)
            assertContains(imports.toList(), ModulePropertiesConfig::class.java.name)
        }

        @Test
        fun `skips routing when disabled`() {
            val imports = selector.selectImports(metadataOf<RoutingDisabled>())
            assertEquals(
                listOf(ModuleSchemaConfig::class.java.name, ModulePropertiesConfig::class.java.name),
                imports.toList(),
            )
        }

        @Test
        fun `skips schemas when disabled`() {
            val imports = selector.selectImports(metadataOf<SchemasDisabled>())
            assertEquals(
                listOf(ModuleRoutingConfig::class.java.name, ModulePropertiesConfig::class.java.name),
                imports.toList(),
            )
        }

        @Test
        fun `skips properties when disabled`() {
            val imports = selector.selectImports(metadataOf<PropertiesDisabled>())
            assertEquals(
                listOf(ModuleRoutingConfig::class.java.name, ModuleSchemaConfig::class.java.name),
                imports.toList(),
            )
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

@EnableAppModules(properties = false)
private class PropertiesDisabled

@EnableAppModules(routing = false, schemas = false, properties = false)
private class AllDisabled
