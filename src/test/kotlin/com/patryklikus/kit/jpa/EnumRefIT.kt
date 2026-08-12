package com.patryklikus.kit.jpa

import com.patryklikus.kit.testapp.enumref.Colour
import com.patryklikus.kit.testapp.enumref.Shirt
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

    @Test
    @Transactional
    fun `lookup table is seeded with one row per constant`() {
        val count = em.createNativeQuery("select count(*) from enumref.colour").singleResult as Number
        assertEquals(3, count.toInt())
        val red = em.createNativeQuery("select id from enumref.colour where name = 'RED'").singleResult as Number
        assertEquals(1, red.toInt())
    }

    @Test
    @Transactional
    fun `round-trips an enum value through the foreign key`() {
        val saved = Shirt(Colour.GREEN).also(em::persist)
        em.flush(); em.clear()
        assertEquals(Colour.GREEN, em.find(Shirt::class.java, saved.id).colour)
    }

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
