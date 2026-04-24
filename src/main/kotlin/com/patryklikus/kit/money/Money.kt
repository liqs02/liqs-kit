package com.patryklikus.kit.money

import java.math.BigDecimal
import java.math.RoundingMode

/** Monetary amount in [currency]'s minor units. Arithmetic is exact. */
data class Money internal constructor(val amount: Long, val currency: Currency) {

    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Cannot add $currency and ${other.currency}" }
        return Money(Math.addExact(amount, other.amount), currency)
    }

    operator fun minus(other: Money): Money {
        require(currency == other.currency) { "Cannot subtract $currency and ${other.currency}" }
        return Money(Math.subtractExact(amount, other.amount), currency)
    }

    operator fun times(n: Long): Money = Money(Math.multiplyExact(amount, n), currency)

    operator fun compareTo(other: Money): Int {
        require(currency == other.currency) { "Cannot compare $currency and ${other.currency}" }
        return amount.compareTo(other.amount)
    }

    fun toDecimal(): BigDecimal = BigDecimal.valueOf(amount, currency.fractionDigits)

    override fun toString(): String = "${toDecimal().toPlainString()} ${currency.code}"

    companion object {
        /**
         * Parses a major-unit decimal string into a [Money]; never silently rounds.
         *
         * @throws ArithmeticException when [amount] carries more decimal places
         * than the currency's [Currency.fractionDigits].
         */
        fun of(amount: String, currency: Currency): Money {
            val scaled = BigDecimal(amount).setScale(currency.fractionDigits, RoundingMode.UNNECESSARY)
            return Money(scaled.movePointRight(currency.fractionDigits).longValueExact(), currency)
        }
    }
}
