package com.patryklikus.kit.spring.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

/**
 * Runs the checks declared by [SafeText]. Returns `true` for `null` to follow Jakarta
 * Bean Validation convention — pair with `@NotNull` if non-null is required.
 */
class SafeTextValidator : ConstraintValidator<SafeText, String> {

    private var minLength: Int = 0
    private var maxLength: Int = Int.MAX_VALUE
    private var pattern: Regex? = null

    override fun initialize(constraint: SafeText) {
        require(constraint.minLength >= 0) { "minLength must be >= 0" }
        require(constraint.maxLength >= constraint.minLength) { "maxLength must be >= minLength" }
        this.minLength = constraint.minLength
        this.maxLength = constraint.maxLength
        this.pattern = constraint.pattern.takeIf { it.isNotEmpty() }?.toRegex()
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return true
        if (value.length < minLength) return false
        if (value.length > maxLength) return false
        val regex = pattern
        if (regex != null && !regex.matches(value)) return false
        return true
    }
}
