package com.patryklikus.kit.jpa

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private enum class Colour { RED, GREEN }

private enum class Planet {
    EARTH { override fun habitable() = true },
    MARS { override fun habitable() = false };
    abstract fun habitable(): Boolean
}

class EnumRefRegistryTest {
    @AfterTest fun tearDown() = EnumRefRegistry.clear()

    @Test fun `maps value to id and back`() {
        EnumRefRegistry.register(Colour::class.java, mapOf("RED" to 1, "GREEN" to 2))
        assertEquals(2, EnumRefRegistry.idOf(Colour.GREEN))
        assertEquals(Colour.RED, EnumRefRegistry.valueOf(Colour::class.java, 1))
    }

    @Test fun `unknown id fails loudly`() {
        EnumRefRegistry.register(Colour::class.java, mapOf("RED" to 1, "GREEN" to 2))
        assertFailsWith<IllegalStateException> { EnumRefRegistry.valueOf(Colour::class.java, 9) }
    }

    @Test fun `isSeeded reflects registration`() {
        assertFalse(EnumRefRegistry.isSeeded(Colour::class.java))
        EnumRefRegistry.register(Colour::class.java, mapOf("RED" to 1, "GREEN" to 2))
        assertTrue(EnumRefRegistry.isSeeded(Colour::class.java))
    }

    @Test fun `register fails when a constant has no seeded id`() {
        assertFailsWith<IllegalStateException> {
            EnumRefRegistry.register(Colour::class.java, mapOf("RED" to 1)) // GREEN missing
        }
    }

    @Test fun `idOf resolves constants that have bodies`() {
        EnumRefRegistry.register(Planet::class.java, mapOf("EARTH" to 1, "MARS" to 2))
        assertEquals(1, EnumRefRegistry.idOf(Planet.EARTH))
        assertEquals(2, EnumRefRegistry.idOf(Planet.MARS))
    }
}
