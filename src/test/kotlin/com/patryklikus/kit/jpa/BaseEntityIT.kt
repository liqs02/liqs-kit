package com.patryklikus.kit.jpa

import com.patryklikus.kit.testapp.sample.TestItem
import com.patryklikus.kit.testapp.sample.TestItemRepository
import com.patryklikus.kit.testutil.Integration
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Integration
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class BaseEntityIT @Autowired constructor(
    private val testItemRepository: TestItemRepository
) {

    @Nested
    inner class Id {

        @Test
        fun `is assigned after save`() {
            val item = testItemRepository.save(TestItem("test"))
            assertTrue(item.id > 0)
        }

        @Test
        fun `is consistent after findById`() {
            val saved = testItemRepository.save(TestItem("findme"))
            val found = testItemRepository.findOne(saved.id) ?: error("not found")
            assertEquals(saved.id, found.id)
            assertEquals("findme", found.name)
        }

        @Test
        fun `throws when accessed before persist`() {
            assertThrows<IllegalArgumentException> {
                TestItem("unpersisted").id
            }
        }
    }

    @Nested
    inner class CreatedAt {

        @Test
        fun `is assigned after save`() {
            val before = Instant.now()
            val item = testItemRepository.save(TestItem("test"))
            assertTrue(!item.createdAt.isBefore(before))
        }

        @Test
        fun `is consistent after findById`() {
            val saved = testItemRepository.save(TestItem("findme"))
            val found = testItemRepository.findOne(saved.id) ?: error("not found")
            assertEquals(saved.createdAt, found.createdAt)
        }

        @Test
        fun `throws when accessed before persist`() {
            assertThrows<IllegalArgumentException> {
                TestItem("unpersisted").createdAt
            }
        }
    }

    @Nested
    inner class UpdatedAt {

        @Test
        fun `is assigned after save`() {
            val before = Instant.now()
            val item = testItemRepository.save(TestItem("test"))
            assertTrue(!item.updatedAt.isBefore(before))
        }

        @Test
        fun `equals createdAt on first save`() {
            val item = testItemRepository.save(TestItem("test"))
            assertEquals(item.createdAt, item.updatedAt)
        }

        @Test
        fun `throws when accessed before persist`() {
            assertThrows<IllegalArgumentException> {
                TestItem("unpersisted").updatedAt
            }
        }
    }
}
