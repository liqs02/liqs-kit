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
 * Requires `jakarta.persistence.create-database-schemas=true` so Hibernate creates the schemas before tables.
 */
class ModuleSchemaIntegrator(private val modules: ApplicationModules) : Integrator {
    override fun integrate(
        metadata: Metadata,
        bootstrapContext: BootstrapContext,
        sessionFactory: SessionFactoryImplementor
    ) {
        val moduleByPackage = modules.associate {
            it.basePackage.name to it.identifier.toString()
        }
        val usedSchemas = mutableSetOf<String>()
        metadata.entityBindings.forEach { persistentClass ->
            if (persistentClass.table.schema != null) return@forEach
            val packageName = persistentClass.mappedClass.packageName
            val schema = moduleByPackage.entries
                .firstOrNull { packageName.startsWith(it.key) }
                ?.value
            if (schema != null) {
                persistentClass.table.schema = schema
                usedSchemas.add(schema)
            }
        }
        usedSchemas.forEach { schema ->
            metadata.database.locateNamespace(null, Identifier.toIdentifier(schema))
        }
    }

    override fun disintegrate(
        sessionFactory: SessionFactoryImplementor,
        serviceRegistry: SessionFactoryServiceRegistry
    ) {
    }
}
