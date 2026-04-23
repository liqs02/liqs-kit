package com.patryklikus.kit.money

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CurrencyTest {

    @Test
    fun `EUR has two fraction digits`() {
        assertEquals(2, Currency.EUR.fractionDigits)
        assertEquals("EUR", Currency.EUR.code)
    }

    @Test
    fun `PLN has two fraction digits`() {
        assertEquals(2, Currency.PLN.fractionDigits)
        assertEquals("PLN", Currency.PLN.code)
    }

    @Test
    fun `JPY has zero fraction digits`() {
        assertEquals(0, Currency.JPY.fractionDigits)
        assertEquals("JPY", Currency.JPY.code)
    }

    @Test
    fun `BHD has three fraction digits`() {
        assertEquals(3, Currency.BHD.fractionDigits)
        assertEquals("BHD", Currency.BHD.code)
    }

    @Test
    fun `enum covers every ISO code the JDK exposes`() {
        val jdkCodes = java.util.Currency.getAvailableCurrencies()
            .map { it.currencyCode }
            .filter { it.length == 3 && it.all(Char::isLetter) }
            .toSet()
        val enumCodes = Currency.entries.map { it.code }.toSet()
        assertEquals(jdkCodes, enumCodes)
    }
}
