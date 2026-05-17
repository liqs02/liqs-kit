package com.patryklikus.kit.jpa

import com.patryklikus.kit.testutil.InternalIntegration
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@InternalIntegration
class KotlinNullabilityIntegratorIT(
    private val em: EntityManager,
) {
    @Test
    fun `non-nullable Kotlin reference type becomes NOT NULL column`() {
        assertEquals("NO", isNullable("non_null_string"))
    }

    @Test
    fun `nullable Kotlin reference type stays NULL column`() {
        assertEquals("YES", isNullable("nullable_string"))
    }

    @Test
    fun `non-nullable Kotlin primitive remains NOT NULL column`() {
        assertEquals("NO", isNullable("non_null_int"))
    }

    @Test
    fun `nullable Kotlin primitive stays NULL column`() {
        assertEquals("YES", isNullable("nullable_int"))
    }

    @Test
    fun `inherited BaseEntity id column is NOT NULL`() {
        assertEquals("NO", isNullable("_id"))
    }

    private fun isNullable(columnName: String): String =
        em.createNativeQuery(
            "SELECT is_nullable FROM information_schema.columns " +
                "WHERE table_name = 'nullability_probe' AND column_name = :col"
        )
            .setParameter("col", columnName)
            .singleResult as String
}
