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
        fun `imports both configs when routing and schemas enabled`() {
            val imports = selector.selectImports(metadataOf<BothEnabled>())
            assertEquals(2, imports.size)
            assertContains(imports.toList(), ModuleRoutingConfig::class.java.name)
            assertContains(imports.toList(), ModuleSchemaConfig::class.java.name)
        }

        @Test
        fun `imports only schema config when routing disabled`() {
            val imports = selector.selectImports(metadataOf<RoutingDisabled>())
            assertEquals(listOf(ModuleSchemaConfig::class.java.name), imports.toList())
        }

        @Test
        fun `imports only routing config when schemas disabled`() {
            val imports = selector.selectImports(metadataOf<SchemasDisabled>())
            assertEquals(listOf(ModuleRoutingConfig::class.java.name), imports.toList())
        }

        @Test
        fun `imports nothing when both disabled`() {
            val imports = selector.selectImports(metadataOf<BothDisabled>())
            assertEquals(0, imports.size)
        }
    }

    private inline fun <reified T : Any> metadataOf(): AnnotationMetadata =
        AnnotationMetadata.introspect(T::class.java)
}

@EnableAppModules
private class BothEnabled

@EnableAppModules(routing = false)
private class RoutingDisabled

@EnableAppModules(schemas = false)
private class SchemasDisabled

@EnableAppModules(routing = false, schemas = false)
private class BothDisabled
