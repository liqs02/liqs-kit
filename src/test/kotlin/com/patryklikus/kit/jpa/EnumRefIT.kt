package com.patryklikus.kit.jpa

import com.patryklikus.kit.testutil.InternalIntegration
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@InternalIntegration
class EnumRefIT(
    private val em: EntityManager,
) {
    @Test
    @Transactional
    fun `lookup table id column is smallint`() {
        val type = em.createNativeQuery(
            "select data_type from information_schema.columns where table_name='colour' and column_name='id'"
        ).singleResult
        assertEquals("smallint", type)
    }

    @Test
    @Transactional
    fun `entity column is smallint with a foreign key`() {
        val colType = em.createNativeQuery(
            "select data_type from information_schema.columns where table_name='shirt' and column_name='colour'"
        ).singleResult
        assertEquals("smallint", colType)
        val fks = em.createNativeQuery(
            "select count(*) from information_schema.table_constraints " +
                "where table_name='shirt' and constraint_type='FOREIGN KEY'"
        ).singleResult as Number
        assertEquals(1, fks.toInt())
    }

    // NOTE: seeded-row count and the persist/find round-trip are asserted in Task 7 (they need the seeder).

    @Test
    @Transactional
    fun `two entities sharing colour produce one lookup table and two FKs`() {
        val tables = em.createNativeQuery(
            "select count(*) from information_schema.tables where table_name='colour'"
        ).singleResult as Number
        assertEquals(1, tables.toInt())
        val fks = em.createNativeQuery(
            "select count(*) from information_schema.table_constraints " +
                "where table_name in ('shirt','flag') and constraint_type='FOREIGN KEY'"
        ).singleResult as Number
        assertEquals(2, fks.toInt())
    }
}
