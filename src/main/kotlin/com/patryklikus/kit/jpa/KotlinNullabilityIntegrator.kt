package com.patryklikus.kit.jpa

import org.hibernate.boot.Metadata
import org.hibernate.boot.spi.BootstrapContext
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.integrator.spi.Integrator
import org.hibernate.mapping.PersistentClass
import org.hibernate.mapping.Property
import org.hibernate.service.spi.SessionFactoryServiceRegistry
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

/**
 * Marks columns backing non-nullable Kotlin reference-typed properties as `NOT NULL` in the generated DDL.
 *
 * Hibernate handles Kotlin primitives natively; reference types (`String`, `Instant`, ...) need this gap closed.
 */
class KotlinNullabilityIntegrator : Integrator {

    override fun integrate(
        metadata: Metadata,
        bootstrapContext: BootstrapContext,
        sessionFactory: SessionFactoryImplementor,
    ) {
        metadata.entityBindings.forEach { applyTo(it) }
    }

    override fun disintegrate(
        sessionFactory: SessionFactoryImplementor,
        serviceRegistry: SessionFactoryServiceRegistry,
    ) {
    }

    private fun applyTo(persistentClass: PersistentClass) {
        val mappedClass = persistentClass.mappedClass ?: return
        val kotlinProperties = collectKotlinProperties(mappedClass)
        persistentClass.propertyClosure.forEach { property ->
            val kotlinProperty = kotlinProperties[property.name] ?: return@forEach
            if (kotlinProperty.returnType.isMarkedNullable) return@forEach
            forceNonNull(property)
        }
    }

    /** Collects Kotlin member properties by name, walking Kotlin supertypes; Java classes are skipped to avoid platform-type false positives. */
    private fun collectKotlinProperties(mappedClass: Class<*>): Map<String, KProperty1<*, *>> {
        val byName = mutableMapOf<String, KProperty1<*, *>>()
        var current: Class<*>? = mappedClass
        while (current != null && current != Any::class.java) {
            if (current.isAnnotationPresent(kotlin.Metadata::class.java)) {
                kotlinPropertiesByName(current.kotlin).forEach { (name, prop) ->
                    byName.putIfAbsent(name, prop)
                }
            }
            current = current.superclass
        }
        return byName
    }

    private fun kotlinPropertiesByName(kClass: KClass<*>): Map<String, KProperty1<*, *>> =
        try {
            kClass.memberProperties.associateBy { it.name }
        } catch (_: Throwable) {
            // Some generated/synthetic classes are not introspectable by kotlin-reflect.
            emptyMap()
        }

    private fun forceNonNull(property: Property) {
        property.isOptional = false
        property.value.columns.forEach { column ->
            if (column.isNullable) {
                column.isNullable = false
            }
        }
    }
}
