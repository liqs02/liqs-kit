package com.patryklikus.kit.spring.exception

import kotlin.reflect.KClass

class NoAssociation(entity: KClass<*>) :
    ApiError(ErrorType.NoAssociation, "Associated ${entity.simpleName?.lowercase()} not found")
