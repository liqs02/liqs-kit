package com.patryklikus.kit.jpa

import org.hibernate.boot.Metadata

/**
 * Walks the bound entity mappings and returns every `@EnumRef` site.
 *
 * A property is an `@EnumRef` site when its backing JVM field carries [EnumRef] (the annotation is
 * `@Target(FIELD)`, so it is visible to Java reflection — see Spike Decision #1). Shared by the
 * converter contributor, the integrator, and the seeder so all three agree on the exact
 * `(entity, property, enum type, table name)` tuples.
 */
fun enumRefFields(metadata: Metadata): List<EnumRefField> {
    val fields = mutableListOf<EnumRefField>()
    metadata.entityBindings.forEach { persistentClass ->
        val entityClass = persistentClass.mappedClass ?: return@forEach
        persistentClass.propertyClosure.forEach { property ->
            val field = runCatching { entityClass.getDeclaredField(property.name) }.getOrNull()
                ?: return@forEach
            val annotation = field.getAnnotation(EnumRef::class.java) ?: return@forEach
            @Suppress("UNCHECKED_CAST")
            val enumType = field.type as Class<out Enum<*>>
            fields += EnumRefField(
                enumType = enumType,
                tableName = tableNameFor(enumType, annotation.name),
                entityClass = entityClass,
                propertyName = property.name,
            )
        }
    }
    return fields
}

/**
 * Boot-populated registry of every discovered `@EnumRef` enum type and its lookup table name.
 *
 * Filled by [EnumRefIntegrator] during `integrate()` (before Spring's `SmartInitializingSingleton`
 * callbacks fire) and read by the seeder (Task 7) so it reuses the exact names the DDL created
 * instead of recomputing them.
 */
object EnumRefTypes {
    private val tablesByType = LinkedHashMap<Class<out Enum<*>>, String>()

    @Synchronized
    fun register(type: Class<out Enum<*>>, tableName: String) {
        tablesByType[type] = tableName
    }

    @Synchronized
    fun all(): Map<Class<out Enum<*>>, String> = LinkedHashMap(tablesByType)

    @Synchronized
    fun clear() = tablesByType.clear()
}
