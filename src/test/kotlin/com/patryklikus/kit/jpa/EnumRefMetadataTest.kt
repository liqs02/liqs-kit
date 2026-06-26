package com.patryklikus.kit.jpa

import kotlin.test.Test
import kotlin.test.assertEquals

private enum class PaymentStatus { NEW }

class EnumRefMetadataTest {
    @Test fun `derives table name from enum simple name`() {
        assertEquals("paymentstatus", tableNameFor(PaymentStatus::class.java, ""))
    }

    @Test fun `honours explicit override`() {
        assertEquals("payment_status", tableNameFor(PaymentStatus::class.java, "payment_status"))
    }
}
