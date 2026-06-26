package com.patryklikus.kit.jpa

import jakarta.persistence.AttributeConverter

/**
 * Maps an `@EnumRef` enum to its seeded `smallint` id via [EnumRefRegistry]. One instance per enum type.
 *
 * Boot-safe: before the seeder runs (e.g. when Hibernate renders the enum CHECK constraint at metadata build),
 * the registry is not yet seeded for [type], so it falls back to `ordinal + 1` / `enumConstants[id-1]`. The seeder
 * assigns ids `1..N` in enum declaration order, so these fallbacks equal the real seeded ids.
 */
class EnumRefConverter(private val type: Class<out Enum<*>>) : AttributeConverter<Enum<*>, Short> {
    override fun convertToDatabaseColumn(attribute: Enum<*>?): Short? = attribute?.let {
        if (EnumRefRegistry.isSeeded(type)) EnumRefRegistry.idOf(it) else (it.ordinal + 1).toShort()
    }

    override fun convertToEntityAttribute(dbData: Short?): Enum<*>? = dbData?.let { id ->
        if (EnumRefRegistry.isSeeded(type)) EnumRefRegistry.valueOf(type, id)
        else type.enumConstants[id - 1]
    }
}
