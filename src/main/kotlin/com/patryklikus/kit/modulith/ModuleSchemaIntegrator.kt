package com.patryklikus.kit.modulith

import org.hibernate.boot.Metadata
import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.spi.BootstrapContext
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.integrator.spi.Integrator
import org.hibernate.service.spi.SessionFactoryServiceRegistry
import org.springframework.modulith.core.ApplicationModules

/**
 * Derives each entity's DB schema from its module package (`@AppModule.id`).
 *
 * Nested modules share their root module's schema (`location.address` → schema `location`),
 * so cross-schema foreign keys between parent and child stay flat.
 *
 * Requires `jakarta.persistence.create-database-schemas=true` so Hibernate creates the schemas before tables.
 */
class ModuleSchemaIntegrator(private val modules: ApplicationModules) : Integrator {
    override fun integrate(
        metadata: Metadata,
        bootstrapContext: BootstrapContext,
        sessionFactory: SessionFactoryImplementor
    ) {
        val modulesBySpecificity = modules.sortedByDescending { it.basePackage.name.length }
        val usedSchemas = mutableSetOf<String>()
        metadata.entityBindings.forEach { persistentClass ->
            if (persistentClass.table.schema != null) return@forEach
            val packageName = persistentClass.mappedClass.packageName
            val module = modulesBySpecificity.firstOrNull { packageName.isInPackage(it.basePackage.name) }
                ?: return@forEach
            val schema = module.identifier.toString().substringBefore('.')
            persistentClass.table.schema = schema
            usedSchemas.add(schema)
        }
        usedSchemas.forEach { schema ->
            metadata.database.locateNamespace(null, Identifier.toIdentifier(schema))
        }
    }

    private fun String.isInPackage(base: String): Boolean = this == base || startsWith("$base.")

    override fun disintegrate(
        sessionFactory: SessionFactoryImplementor,
        serviceRegistry: SessionFactoryServiceRegistry
    ) {
    }
}
