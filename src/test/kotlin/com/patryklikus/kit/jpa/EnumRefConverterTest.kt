package com.patryklikus.kit.jpa

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private enum class Size { S, M, L }

class EnumRefConverterTest {
    @AfterTest fun tearDown() = EnumRefRegistry.clear()

    @Test fun `round-trips through the registry when seeded`() {
        EnumRefRegistry.register(Size::class.java, mapOf("S" to 1, "M" to 2, "L" to 3))
        val converter = EnumRefConverter(Size::class.java)
        assertEquals(2, converter.convertToDatabaseColumn(Size.M))
        assertEquals(Size.L, converter.convertToEntityAttribute(3))
    }

    @Test fun `falls back to ordinal+1 when unseeded (boot path)`() {
        val converter = EnumRefConverter(Size::class.java) // registry empty
        assertEquals(1, converter.convertToDatabaseColumn(Size.S))
        assertEquals(3, converter.convertToDatabaseColumn(Size.L))
        assertEquals(Size.M, converter.convertToEntityAttribute(2))
    }

    @Test fun `nulls pass through`() {
        val converter = EnumRefConverter(Size::class.java)
        assertNull(converter.convertToDatabaseColumn(null))
        assertNull(converter.convertToEntityAttribute(null))
    }
}
