package io.extact.rms.application.domain.constraint;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

/**
 * 予約開始日時チェックアノテーション。
 * 登録時にのみ利用する
 * <pre>
 * ・nullでないこと
 * ・現在日時より未来であること
 * </pre>
 */
@Documented
@Constraint(validatedBy = {})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@NotNull
@Future
public @interface ReserveStartDateTimeFuture {
    String message() default "{io.extact.rms.service.contrains.Generic.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        ReserveStartDateTimeFuture[] value();
    }
}
