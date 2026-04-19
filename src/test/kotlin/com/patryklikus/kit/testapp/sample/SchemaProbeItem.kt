package com.patryklikus.kit.testapp.sample

import com.patryklikus.kit.jpa.BaseEntity
import jakarta.persistence.Entity

@Entity
class SchemaProbeItem(
    val name: String = ""
) : BaseEntity()
