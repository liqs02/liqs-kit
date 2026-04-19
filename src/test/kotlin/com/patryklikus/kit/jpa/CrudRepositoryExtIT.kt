package com.patryklikus.kit.jpa

import com.patryklikus.kit.testapp.sample.TestItem
import com.patryklikus.kit.testapp.sample.TestItemRepository
import com.patryklikus.kit.testutil.Integration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Integration
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class CrudRepositoryExtIT @Autowired constructor(
    private val repository: TestItemRepository
) {
    @Test
    fun `returns entity when id exists`() {
        val saved = repository.save(TestItem("hello"))

        val found = repository.findOne(saved.id)

        assertEquals(saved.id, found?.id)
        assertEquals("hello", found?.name)
    }

    @Test
    fun `returns null when id does not exist`() {
        assertNull(repository.findOne(-1L))
    }
}
