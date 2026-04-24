package com.patryklikus.kit.spring.exception

import kotlin.reflect.KClass

/**
 * Entity with the given field value already exists (e.g. unique constraint violation).
 *
 * @param entity class of the conflicting entity.
 * @param field name of the field that collided.
 */
class Conflict(entity: KClass<*>, field: String) :
    ApiError(ErrorType.Conflict, "${entity.simpleName?.lowercase()} with provided $field already exists")
