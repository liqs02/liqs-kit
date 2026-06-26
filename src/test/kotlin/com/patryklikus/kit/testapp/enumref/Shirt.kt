package com.patryklikus.kit.testapp.enumref

import com.patryklikus.kit.jpa.BaseEntity
import com.patryklikus.kit.jpa.EnumRef
import jakarta.persistence.Entity

@Entity
class Shirt(@EnumRef val colour: Colour) : BaseEntity()
