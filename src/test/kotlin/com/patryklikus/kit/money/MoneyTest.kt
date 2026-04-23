package com.patryklikus.kit.money

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MoneyTest {

    @Nested
    inner class Of {
        @Test
        fun `parses EUR amount with two decimals`() {
            assertEquals(Money(12345, Currency.EUR), Money.of("123.45", Currency.EUR))
        }

        @Test
        fun `parses PLN amount with two decimals`() {
            assertEquals(Money(10000, Currency.PLN), Money.of("100.00", Currency.PLN))
        }

        @Test
        fun `parses PLN integer amount by scaling up to minor units`() {
            assertEquals(Money(10000, Currency.PLN), Money.of("100", Currency.PLN))
        }

        @Test
        fun `parses JPY amount without decimals`() {
            assertEquals(Money(500, Currency.JPY), Money.of("500", Currency.JPY))
        }

        @Test
        fun `parses BHD amount with three decimals`() {
            assertEquals(Money(12345, Currency.BHD), Money.of("12.345", Currency.BHD))
        }

        @Test
        fun `throws ArithmeticException when EUR string has three decimals`() {
            assertFailsWith<ArithmeticException> { Money.of("1.234", Currency.EUR) }
        }

        @Test
        fun `throws ArithmeticException when JPY string has any fraction`() {
            assertFailsWith<ArithmeticException> { Money.of("500.5", Currency.JPY) }
        }

        @Test
        fun `throws ArithmeticException when BHD string has four decimals`() {
            assertFailsWith<ArithmeticException> { Money.of("1.2345", Currency.BHD) }
        }
    }

    @Nested
    inner class Plus {
        @Test
        fun `adds amounts in the same currency`() {
            val sum = Money(12345, Currency.EUR) + Money(55, Currency.EUR)
            assertEquals(Money(12400, Currency.EUR), sum)
        }

        @Test
        fun `throws IllegalArgumentException for cross-currency addition`() {
            assertFailsWith<IllegalArgumentException> {
                Money(100, Currency.EUR) + Money(100, Currency.PLN)
            }
        }

        @Test
        fun `throws ArithmeticException on overflow`() {
            assertFailsWith<ArithmeticException> {
                Money(Long.MAX_VALUE, Currency.EUR) + Money(1, Currency.EUR)
            }
        }
    }

    @Nested
    inner class Minus {
        @Test
        fun `subtracts amounts in the same currency`() {
            val diff = Money(12345, Currency.EUR) - Money(45, Currency.EUR)
            assertEquals(Money(12300, Currency.EUR), diff)
        }

        @Test
        fun `throws IllegalArgumentException for cross-currency subtraction`() {
            assertFailsWith<IllegalArgumentException> {
                Money(100, Currency.EUR) - Money(100, Currency.PLN)
            }
        }

        @Test
        fun `throws ArithmeticException on underflow`() {
            assertFailsWith<ArithmeticException> {
                Money(Long.MIN_VALUE, Currency.EUR) - Money(1, Currency.EUR)
            }
        }
    }

    @Nested
    inner class Times {
        @Test
        fun `multiplies by scalar`() {
            val product = Money(250, Currency.EUR) * 4L
            assertEquals(Money(1000, Currency.EUR), product)
        }

        @Test
        fun `throws ArithmeticException on overflow`() {
            assertFailsWith<ArithmeticException> {
                Money(Long.MAX_VALUE, Currency.EUR) * 2L
            }
        }
    }

    @Nested
    inner class CompareTo {
        @Test
        fun `compares amounts in the same currency`() {
            val small = Money(100, Currency.EUR)
            val big = Money(200, Currency.EUR)
            assertTrue(small < big)
            assertTrue(big > small)
            assertEquals(0, small.compareTo(Money(100, Currency.EUR)))
        }

        @Test
        fun `throws IllegalArgumentException for cross-currency comparison`() {
            assertFailsWith<IllegalArgumentException> {
                Money(100, Currency.EUR).compareTo(Money(100, Currency.PLN))
            }
        }
    }

    @Nested
    inner class ToDecimal {
        @Test
        fun `returns EUR major units with two decimal places`() {
            assertEquals(BigDecimal("123.45"), Money(12345, Currency.EUR).toDecimal())
        }

        @Test
        fun `returns JPY major units without fraction`() {
            assertEquals(BigDecimal("500"), Money(500, Currency.JPY).toDecimal())
        }

        @Test
        fun `returns BHD major units with three decimal places`() {
            assertEquals(BigDecimal("12.345"), Money(12345, Currency.BHD).toDecimal())
        }
    }

    @Nested
    inner class ToStringFormat {
        @Test
        fun `formats EUR as decimal with currency code`() {
            assertEquals("123.45 EUR", Money(12345, Currency.EUR).toString())
        }

        @Test
        fun `formats JPY without fraction digits`() {
            assertEquals("500 JPY", Money(500, Currency.JPY).toString())
        }

        @Test
        fun `formats BHD with three fraction digits`() {
            assertEquals("12.345 BHD", Money(12345, Currency.BHD).toString())
        }
    }
}
