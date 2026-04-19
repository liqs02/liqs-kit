package com.patryklikus.kit.testapp.sample

import com.patryklikus.kit.jpa.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "test_item")
class TestItem(
    val name: String
) : BaseEntity()
