package com.patryklikus.kit.spring.validation

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SafeTextValidatorTest {

    private val validator: Validator =
        Validation.buildDefaultValidatorFactory().validator

    private data class Holder(@field:SafeText val value: String?)

    private data class WithBounds(
        @field:SafeText(minLength = 3, maxLength = 5) val value: String?,
    )

    private data class WithPattern(
        @field:SafeText(pattern = "^[a-z]+$") val value: String?,
    )

    private data class WithEmptyPattern(
        @field:SafeText(pattern = "") val value: String?,
    )

    private data class WithShort(@field:ShortText val value: String?)

    private data class WithMedium(@field:MediumText val value: String?)

    private data class WithLong(@field:LongText val value: String?)

    @Nested
    inner class Null {
        @Test
        fun `passes when value is null`() {
            assertTrue(validator.validate(Holder(null)).isEmpty())
        }
    }

    @Nested
    inner class Length {
        @Test
        fun `passes when length is within bounds`() {
            assertTrue(validator.validate(WithBounds("abcd")).isEmpty())
        }

        @Test
        fun `passes when length equals maxLength`() {
            assertTrue(validator.validate(WithBounds("abcde")).isEmpty())
        }

        @Test
        fun `passes when length equals minLength`() {
            assertTrue(validator.validate(WithBounds("abc")).isEmpty())
        }

        @Test
        fun `fails when length exceeds maxLength`() {
            val violations = validator.validate(WithBounds("abcdef"))
            assertEquals(1, violations.size)
        }

        @Test
        fun `fails when length is below minLength`() {
            val violations = validator.validate(WithBounds("ab"))
            assertEquals(1, violations.size)
        }

        @Test
        fun `default maxLength rejects oversized payloads`() {
            val violations = validator.validate(Holder("x".repeat(257)))
            assertEquals(1, violations.size)
        }

        @Test
        fun `default maxLength accepts payloads at the cap`() {
            assertTrue(validator.validate(Holder("x".repeat(256))).isEmpty())
        }
    }

    @Nested
    inner class Pattern {
        @Test
        fun `passes when value matches pattern`() {
            assertTrue(validator.validate(WithPattern("abc")).isEmpty())
        }

        @Test
        fun `fails when value does not match pattern`() {
            val violations = validator.validate(WithPattern("ABC"))
            assertEquals(1, violations.size)
        }

        @Test
        fun `empty pattern means no pattern check`() {
            assertTrue(validator.validate(WithEmptyPattern("anything 123 ???")).isEmpty())
        }
    }

    private data class NegativeMinLength(
        @field:SafeText(minLength = -1) val value: String?,
    )

    private data class MaxBelowMin(
        @field:SafeText(minLength = 10, maxLength = 5) val value: String?,
    )

    @Nested
    inner class Initialization {
        @Test
        fun `rejects negative minLength`() {
            assertFailsWith<jakarta.validation.ValidationException> {
                validator.validate(NegativeMinLength("x"))
            }
        }

        @Test
        fun `rejects maxLength below minLength`() {
            assertFailsWith<jakarta.validation.ValidationException> {
                validator.validate(MaxBelowMin("x"))
            }
        }
    }

    @Nested
    inner class Shorthands {
        @Test
        fun `ShortText accepts up to 64 chars`() {
            assertTrue(validator.validate(WithShort("x".repeat(64))).isEmpty())
        }

        @Test
        fun `ShortText rejects 65 chars`() {
            val violations = validator.validate(WithShort("x".repeat(65)))
            assertEquals(1, violations.size)
        }

        @Test
        fun `MediumText accepts up to 256 chars`() {
            assertTrue(validator.validate(WithMedium("x".repeat(256))).isEmpty())
        }

        @Test
        fun `MediumText rejects 257 chars`() {
            val violations = validator.validate(WithMedium("x".repeat(257)))
            assertEquals(1, violations.size)
        }

        @Test
        fun `LongText accepts up to 2048 chars`() {
            assertTrue(validator.validate(WithLong("x".repeat(2048))).isEmpty())
        }

        @Test
        fun `LongText rejects 2049 chars`() {
            val violations = validator.validate(WithLong("x".repeat(2049)))
            assertEquals(1, violations.size)
        }
    }
}

