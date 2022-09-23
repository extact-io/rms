package io.extact.rms.platform.jwt.consumer;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.ws.rs.NameBinding;

/**
 * 認証が必要なことを表すアノテーション。
 * このアノテーションが付与されているリソースはメソッド実行前に認証チェックが行われる。
 */
@Inherited
@NameBinding
@Retention(RUNTIME)
@Target({ METHOD, TYPE })
public @interface Authenticated {

}
