package com.patryklikus.kit.jpa

/** A discovered `@EnumRef` mapping site, shared between the integrator and the seeder. */
data class EnumRefField(
    val enumType: Class<out Enum<*>>,
    val tableName: String,
    val entityClass: Class<*>,
    val propertyName: String,
)

/** The lookup table name for an enum type: explicit [override] wins, else the simple name lower-cased. */
fun tableNameFor(enumType: Class<out Enum<*>>, override: String): String =
    if (override.isNotBlank()) override else enumType.simpleName.lowercase()
