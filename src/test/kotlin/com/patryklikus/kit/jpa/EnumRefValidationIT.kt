package com.patryklikus.kit.jpa

import com.patryklikus.kit.testapp.enumref.Colour
import com.patryklikus.kit.testutil.InternalIntegration
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Enumerated
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.orm.jpa.EntityManagerFactoryUtils
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@InternalIntegration
class EnumRefValidationIT(
    private val emf: EntityManagerFactory,
    private val txm: PlatformTransactionManager,
) {

    @AfterEach
    fun cleanupOrphanRow() {
        TransactionTemplate(txm).executeWithoutResult {
            EntityManagerFactoryUtils.getTransactionalEntityManager(emf)
                ?.createNativeQuery("delete from enumref.colour where id = 99")
                ?.executeUpdate()
        }
    }

    @Test
    fun `seeder rejects orphan lookup row with no matching enum constant`() {
        val tx = TransactionTemplate(txm)
        tx.executeWithoutResult {
            val em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf)
                ?: error("No transactional EntityManager available")
            em.createNativeQuery("insert into enumref.colour (id, name) values (99, 'PURPLE')").executeUpdate()
        }

        val seeder = EnumRefSeeder(emf, TransactionTemplate(txm), mapOf(Colour::class.java to "enumref.colour"))
        val exception = assertFailsWith<IllegalStateException> { seeder.afterSingletonsInstantiated() }
        assertTrue(exception.message?.contains("PURPLE") == true, "Expected message to contain 'PURPLE' but was: ${exception.message}")
    }

    @Test
    fun `integrator rejects @EnumRef and @Enumerated on the same property`() {
        val field = EnumRefField(
            enumType = Colour::class.java,
            tableName = "colour",
            entityClass = ConflictEntity::class.java,
            propertyName = "colour",
        )
        val exception = assertFailsWith<IllegalStateException> {
            EnumRefIntegrator().requireNoEnumeratedConflict(field)
        }
        assertTrue(
            exception.message?.contains("@EnumRef") == true && exception.message?.contains("@Enumerated") == true,
            "Expected conflict message but was: ${exception.message}",
        )
    }

    private class ConflictEntity {
        @EnumRef
        @Enumerated
        val colour: Colour = Colour.RED
    }
}
