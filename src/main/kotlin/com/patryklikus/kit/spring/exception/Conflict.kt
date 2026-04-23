package com.patryklikus.kit.spring.exception

import kotlin.reflect.KClass

class Conflict(entity: KClass<*>, field: String) :
    ApiError(ErrorType.Conflict, "${entity.simpleName?.lowercase()} with provided $field already exists")
