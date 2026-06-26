package com.patryklikus.kit.jpa

/**
 * Runtime store of `name↔id` mappings for every `@EnumRef` enum type, populated once at startup
 * by [EnumRefSeeder]. The converter reads it on every persist/load.
 */
object EnumRefRegistry {
    private data class Entry(val idByValue: Map<Enum<*>, Short>, val valueById: Map<Short, Enum<*>>)

    private val entries = HashMap<Class<out Enum<*>>, Entry>()

    @Synchronized
    fun register(type: Class<out Enum<*>>, idsByName: Map<String, Short>) {
        val idByValue = HashMap<Enum<*>, Short>()
        val valueById = HashMap<Short, Enum<*>>()
        for (constant in type.enumConstants) {
            val id = idsByName[constant.name]
                ?: error("No lookup id seeded for ${type.name}.${constant.name}")
            idByValue[constant] = id
            valueById[id] = constant
        }
        entries[type] = Entry(idByValue, valueById)
    }

    fun idOf(value: Enum<*>): Short =
        entry(value.javaClass).idByValue[value] ?: error("No lookup id for $value")

    fun valueOf(type: Class<out Enum<*>>, id: Short): Enum<*> =
        entry(type).valueById[id] ?: error("No ${type.simpleName} for lookup id $id")

    fun isSeeded(type: Class<out Enum<*>>): Boolean = entries.containsKey(type)

    @Synchronized
    fun clear() = entries.clear()

    private fun entry(type: Class<out Enum<*>>) =
        entries[type] ?: error("EnumRef type ${type.name} not registered; seeding did not run")
}
