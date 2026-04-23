package com.patryklikus.kit.spring.exception

import kotlin.reflect.KClass

class NotFound(entity: KClass<*>) : ApiError(ErrorType.NotFound, "${entity.simpleName?.lowercase()} not found")
