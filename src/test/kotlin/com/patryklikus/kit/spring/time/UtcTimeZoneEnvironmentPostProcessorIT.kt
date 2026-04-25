package com.patryklikus.kit.spring.time

import com.patryklikus.kit.testutil.InternalIntegration
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@InternalIntegration
class UtcTimeZoneEnvironmentPostProcessorIT {
    @Test
    fun `default time zone is UTC after Spring context starts`() {
        assertEquals(TimeZone.getTimeZone("UTC"), TimeZone.getDefault())
    }
}
