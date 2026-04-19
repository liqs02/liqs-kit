package com.patryklikus.kit.modulith

import com.patryklikus.kit.testapp.OutsideProperties
import com.patryklikus.kit.testapp.sample.SampleNestedProperties
import com.patryklikus.kit.testapp.sample.SampleProperties
import com.patryklikus.kit.testutil.Integration
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Integration
class ModulePropertiesConfigIT(
    private val sample: SampleProperties,
    private val nested: SampleNestedProperties,
    private val outside: OutsideProperties,
) {
    @Test
    fun `binds under module id and ignores the top-level property when annotation prefix is empty`() {
        assertEquals("hello", sample.greeting)
    }

    @Test
    fun `prepends module id to annotation prefix and ignores the un-prefixed property`() {
        assertEquals("nested-hello", nested.value)
    }

    @Test
    fun `leaves properties outside any module untouched`() {
        assertEquals("outside-hello", outside.value)
    }
}
