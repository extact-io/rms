package io.extact.rms.platform.extension;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * propertyNameから取得した実行時設定とアノテーションに指定された設定(value)が同じ場合に
 * CDIBeanを有効化するアノテーション。
 * 一致しないCDIBeanは無効化する。
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD })
public @interface EnabledIfRuntimeConfig {
    String propertyName();
    String value();
}
