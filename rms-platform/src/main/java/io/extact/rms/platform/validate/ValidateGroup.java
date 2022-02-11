package io.extact.rms.platform.validate;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Inherited
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface ValidateGroup {
    Class<?>[] groups() default {};
}
