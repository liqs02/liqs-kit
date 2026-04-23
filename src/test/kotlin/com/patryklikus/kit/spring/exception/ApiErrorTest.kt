package com.patryklikus.kit.spring.exception

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ApiErrorTest {
    private class SampleEntity

    @Nested
    inner class NotFoundMessage {
        @Test
        fun `formats message using lowercased entity simple name`() {
            val error = NotFound(SampleEntity::class)

            assertEquals("sampleentity not found", error.message)
            assertEquals(ErrorType.NotFound, error.type)
        }
    }

    @Nested
    inner class ConflictMessage {
        @Test
        fun `formats message with entity and field name`() {
            val error = Conflict(SampleEntity::class, "email")

            assertEquals("sampleentity with provided email already exists", error.message)
            assertEquals(ErrorType.Conflict, error.type)
        }
    }

    @Nested
    inner class NoAssociationMessage {
        @Test
        fun `formats message as 'Associated {entity} not found'`() {
            val error = NoAssociation(SampleEntity::class)

            assertEquals("Associated sampleentity not found", error.message)
            assertEquals(ErrorType.NoAssociation, error.type)
        }
    }

    @Nested
    inner class InvalidDataMessage {
        @Test
        fun `passes message through`() {
            val error = InvalidData("bad input")

            assertEquals("bad input", error.message)
            assertEquals(ErrorType.InvalidData, error.type)
        }
    }

    @Nested
    inner class IllegalStateMessage {
        @Test
        fun `passes message through`() {
            val error = IllegalState("invalid state")

            assertEquals("invalid state", error.message)
            assertEquals(ErrorType.IllegalState, error.type)
        }
    }

    @Nested
    inner class ForbiddenMessage {
        @Test
        fun `has empty message`() {
            val error = Forbidden()

            assertEquals("", error.message)
            assertEquals(ErrorType.Forbidden, error.type)
        }
    }

    @Nested
    inner class UnauthorizedMessage {
        @Test
        fun `has empty message`() {
            val error = Unauthorized()

            assertEquals("", error.message)
            assertEquals(ErrorType.Unauthorized, error.type)
        }
    }

}
