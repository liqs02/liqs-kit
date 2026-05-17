package com.patryklikus.kit.spring.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

/**
 * Validates that a `String` parameter or field is safe for use as request input.
 *
 * The primary protection is [maxLength], which caps the number of characters and
 * prevents abuse via oversized payloads. Optionally, a [minLength] lower bound and
 * a regex [pattern] can be enforced.
 *
 * Returns `true` for `null` values — combine with `@NotNull` when a non-null value
 * is required.
 *
 * Intended for use on controller `@RequestParam`, `@PathVariable` and `@RequestBody`
 * string inputs together with `@Validated` on the controller class.
 */
@MustBeDocumented
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPE,
    AnnotationTarget.ANNOTATION_CLASS,
)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [SafeTextValidator::class])
annotation class SafeText(
    val maxLength: Int = 256,
    val minLength: Int = 0,
    val pattern: String = "",
    val message: String = "must be a safe text value",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

/** Shorthand for short single-line inputs (names, identifiers, tags). */
@MustBeDocumented
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@SafeText(maxLength = 64)
annotation class ShortText(
    val message: String = "must be a safe text value",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

/** Shorthand for medium-length single-line inputs (titles, summaries). */
@MustBeDocumented
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@SafeText(maxLength = 256)
annotation class MediumText(
    val message: String = "must be a safe text value",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

/** Shorthand for longer multi-line inputs (descriptions, comments). */
@MustBeDocumented
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@SafeText(maxLength = 2048)
annotation class LongText(
    val message: String = "must be a safe text value",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
