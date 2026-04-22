package com.patryklikus.kit.modulith

import com.patryklikus.kit.testutil.InternalIntegration
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@InternalIntegration
class ModuleSchemaIntegratorIT(
    private val em: EntityManager
) {
    @Test
    fun `entity in sample module gets sample schema`() {
        val schema = em.createNativeQuery(
            "SELECT table_schema FROM information_schema.tables WHERE table_name = 'schema_probe_item'"
        ).singleResult as String
        assertEquals("sample", schema)
    }
}
