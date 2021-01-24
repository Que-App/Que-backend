package app.api.v1.validation

import app.api.v1.pojos.ExchangeRequestPojo
import app.services.LessonService
import org.springframework.beans.factory.annotation.Autowired
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@MustBeDocumented
@Constraint(validatedBy = [MaxIndexValidator::class])
annotation class MaxIndex(
    val max: Int = 30,
    val message: String = "You can only submit an exchange request up to 30 indexes ahead",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class MaxIndexValidator : ConstraintValidator<MaxIndex, ExchangeRequestPojo> {
    @Autowired
    private lateinit var lessonService: LessonService

    private lateinit var annotation: MaxIndex

    override fun initialize(constraintAnnotation: MaxIndex?) {
        annotation = constraintAnnotation!!
    }

    override fun isValid(request: ExchangeRequestPojo?, context: ConstraintValidatorContext?): Boolean {
        if(request == null) return false
        val fromLesson = lessonService.findLesson(request.fromLessonId!!)
        val toLesson = lessonService.findLesson(request.toLessonId!!)

        return request.fromIndex!! <= fromLesson.lessonIndex+annotation.max &&
                request.toIndex!! <= toLesson.lessonIndex+annotation.max
    }

}