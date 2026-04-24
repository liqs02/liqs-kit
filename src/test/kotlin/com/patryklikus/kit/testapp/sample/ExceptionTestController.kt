package com.patryklikus.kit.testapp.sample

import com.patryklikus.kit.spring.exception.*
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.security.access.AccessDeniedException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

data class ValidateForm(@field:NotBlank val name: String?)

class PaymentDeclined(reason: String) : ApiError(ErrorType.IllegalState, reason)

@RestController
@RequestMapping("/exception-test")
@Validated
class ExceptionTestController {
    @GetMapping("/not-found")
    fun notFound(): Nothing = throw NotFound(TestItem::class)

    @GetMapping("/conflict")
    fun conflict(): Nothing = throw Conflict(TestItem::class, "name")

    @GetMapping("/no-association")
    fun noAssociation(): Nothing = throw NoAssociation(TestItem::class)

    @GetMapping("/invalid-data")
    fun invalidData(): Nothing = throw InvalidData("bad input")

    @GetMapping("/illegal-state")
    fun illegalState(): Nothing = throw IllegalState("bad state")

    @GetMapping("/forbidden")
    fun forbidden(): Nothing = throw Forbidden()

    @GetMapping("/unauthorized")
    fun unauthorized(): Nothing = throw Unauthorized()

    @PostMapping("/validate")
    fun validate(@Valid @RequestBody form: ValidateForm): String = form.name!!

    @GetMapping("/constraint")
    fun constraint(@RequestParam @Size(min = 5) value: String): String = value

    @GetMapping("/type-mismatch/{id}")
    fun typeMismatch(@PathVariable id: Long): Long = id

    // maps path without {id} to provoke MissingPathVariableException with @Entity param
    @GetMapping("/missing-entity-path")
    fun missingEntityPath(@PathVariable id: TestItem): String = id.toString()

    @GetMapping("/missing-plain-path")
    fun missingPlainPath(@PathVariable id: String): String = id

    @GetMapping("/access-denied")
    fun accessDenied(): Nothing = throw AccessDeniedException("nope")

    @GetMapping("/unexpected")
    fun unexpected(): Nothing = throw RuntimeException("boom")

    @GetMapping("/custom-api-error")
    fun customApiError(): Nothing = throw PaymentDeclined("insufficient funds")
}
