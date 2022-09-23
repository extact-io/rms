package io.extact.rms.application.domain.constraint;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDateTime;

import io.extact.rms.application.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = { BeforeAfterDateTimeValidator.class })
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface BeforeAfterDateTime {

    String message() default "{io.extact.rms.service.contrains.BeforeAfterDateTime.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String from() default "start datetime";

    String to() default "end datetime";

    @Target({ TYPE, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        BeforeAfterDateTime[] value();
    }

    public static class BeforeAfterDateTimeValidator implements ConstraintValidator<BeforeAfterDateTime, BeforeAfterDateTimeValidatable> {
        public boolean isValid(BeforeAfterDateTimeValidatable bean, ConstraintValidatorContext context) {
            if (bean.getStartDateTime() == null || bean.getEndDateTime() == null) {
                return true; // チェックしない
            }
            return bean.getStartDateTime().isBefore(bean.getEndDateTime());
        }
    }

    public interface BeforeAfterDateTimeValidatable {
        public LocalDateTime getStartDateTime();
        public LocalDateTime getEndDateTime();
    }
}
