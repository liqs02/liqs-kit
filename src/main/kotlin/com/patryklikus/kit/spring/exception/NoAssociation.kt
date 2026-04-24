package com.patryklikus.kit.spring.exception

import kotlin.reflect.KClass

/**
 * An associated entity referenced by the request does not exist.
 *
 * Use this (instead of [NotFound]) when the missing entity is a dependency of the primary
 * resource being created/updated — the caller supplied a bad foreign reference, not a bad URL.
 */
class NoAssociation(entity: KClass<*>) :
    ApiError(ErrorType.NoAssociation, "Associated ${entity.simpleName?.lowercase()} not found")
