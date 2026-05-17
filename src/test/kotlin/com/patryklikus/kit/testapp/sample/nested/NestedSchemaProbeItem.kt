package com.patryklikus.kit.testapp.sample.nested

import com.patryklikus.kit.jpa.BaseEntity
import jakarta.persistence.Entity

@Entity
class NestedSchemaProbeItem(
    val name: String = ""
) : BaseEntity()
