package io.extact.rms.application.domain.constraint;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * シリアル番号チェックアノテーション。
 * <pre>
 * ・未入力でないこと
 * ・半角英数ハイフンのみ
 * ・1文字以上15文字以下
 * </pre>
 */
@Documented
@Constraint(validatedBy = {})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@NotBlank
@Size(max = 15)
@Pattern(regexp = "[0-9a-zA-Z\\-]*", message = "{io.extact.rms.service.contrains.SerialNoCharacter.message}")
public @interface SerialNo {
    String message() default "{io.extact.rms.service.contrains.Generic.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        SerialNo[] value();
    }
}
