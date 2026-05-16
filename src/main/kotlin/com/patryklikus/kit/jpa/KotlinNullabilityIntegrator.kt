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
 * Marks columns backing non-nullable Kotlin properties as `NOT NULL` in the generated DDL.
 *
 * Hibernate already infers `NOT NULL` for Kotlin primitive-backed columns (`Int`, `Long`,
 * `Boolean`, etc.) because they compile to JVM primitives. For reference-typed properties
 * (`String`, `Instant`, embedded entities, enums, ...) the JVM type is identical regardless
 * of Kotlin nullability, so without help Hibernate produces a nullable column even when
 * the Kotlin declaration is `val name: String`.
 *
 * This integrator closes that gap by walking every persistent property, consulting
 * [KProperty1.returnType] via `kotlin-reflect`, and forcing the underlying column(s)
 * to non-nullable whenever the Kotlin side is non-nullable. Properties whose Kotlin type
 * is `T?`, or whose backing field cannot be located via reflection, are left untouched.
 *
 * Discovered automatically by Hibernate through the `java.util.ServiceLoader` registration
 * in `META-INF/services/org.hibernate.integrator.spi.Integrator`.
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

    /**
     * Walks the entity class and its (Kotlin) supertypes - e.g. `BaseEntity` - collecting
     * member properties by name. Java types are skipped because kotlin-reflect reports their
     * fields as platform-typed (non-nullable) regardless of actual nullability, which would
     * yield false positives.
     */
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
