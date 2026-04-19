package com.patryklikus.kit.jpa

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull

/** Returns the entity or null, without wrapping in Optional. */
fun <T : Any, ID : Any> CrudRepository<T, ID>.findOne(id: ID): T? = findByIdOrNull(id)
