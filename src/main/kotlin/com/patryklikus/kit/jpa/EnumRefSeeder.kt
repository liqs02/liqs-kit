package com.patryklikus.kit.jpa

import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.SmartInitializingSingleton
import org.springframework.orm.jpa.EntityManagerFactoryUtils
import org.springframework.transaction.support.TransactionTemplate

class EnumRefSeeder(
    private val emf: EntityManagerFactory,
    private val tx: TransactionTemplate,
    private val tablesByType: Map<Class<out Enum<*>>, String>,
) : SmartInitializingSingleton {

    override fun afterSingletonsInstantiated() {
        tablesByType.forEach { (type, table) -> seed(type, table) }
    }

    private fun seed(type: Class<out Enum<*>>, table: String) = tx.executeWithoutResult {
        val em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf)
            ?: error("No transactional EntityManager available for $table seeding")
        @Suppress("UNCHECKED_CAST")
        val existing = em.createNativeQuery("select id, name from $table")
            .resultList as List<Array<Any>>
        val idByName = existing.associate { (it[1] as String) to (it[0] as Number).toShort() }.toMutableMap()

        val codeNames = type.enumConstants.map { it.name }.toSet()
        val orphans = idByName.keys - codeNames
        check(orphans.isEmpty()) { "Lookup table $table has rows with no matching ${type.name} constant: $orphans" }

        var next = (idByName.values.maxOrNull() ?: 0).toInt()
        for (constant in type.enumConstants) {
            if (constant.name in idByName) continue
            next += 1
            em.createNativeQuery("insert into $table (id, name) values (:id, :name)")
                .setParameter("id", next.toShort()).setParameter("name", constant.name).executeUpdate()
            idByName[constant.name] = next.toShort()
        }
        EnumRefRegistry.register(type, idByName)
    }
}
