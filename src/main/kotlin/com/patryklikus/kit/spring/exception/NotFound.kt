package com.patryklikus.kit.spring.exception

import kotlin.reflect.KClass

/**
 * Target entity does not exist.
 *
 * @param entity class of the missing entity.
 */
class NotFound(entity: KClass<*>) : ApiError(ErrorType.NotFound, "${entity.simpleName?.lowercase()} not found")
