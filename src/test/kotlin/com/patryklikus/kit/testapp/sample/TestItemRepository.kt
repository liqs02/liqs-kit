package com.patryklikus.kit.testapp.sample

import org.springframework.data.jpa.repository.JpaRepository

interface TestItemRepository : JpaRepository<TestItem, Long>
