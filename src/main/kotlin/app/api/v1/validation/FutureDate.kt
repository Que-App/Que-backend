package app.api.v1.validation

import java.sql.Date
import java.time.LocalDate
import java.time.ZoneId
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@MustBeDocumented
@Constraint(validatedBy = [FutureDateValidator::class])
annotation class FutureDate(
    val message: String = "Please specify a future date.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class FutureDateValidator : ConstraintValidator<FutureDate, Any> {
    override fun isValid(value: Any?, context: ConstraintValidatorContext?): Boolean {
        if(value == null ) return false

        return when(value) {
            is Date -> value.toLocalDate().isAfter(LocalDate.now())
            is LocalDate -> value.isAfter(LocalDate.now())
            is java.util.Date -> value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(LocalDate.now())
            else -> throw IllegalStateException("Couldn't interpret ${value::class.java} as date")
        }
    }
}