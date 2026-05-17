package com.patryklikus.kit.testapp.nullability

import com.patryklikus.kit.jpa.BaseEntity
import jakarta.persistence.Entity

@Entity
class NullabilityProbe(
    val nonNullString: String,
    val nullableString: String?,
    val nonNullInt: Int,
    val nullableInt: Int?,
) : BaseEntity()
