package io.extact.rms.platform.jwt.provider;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

/**
 * 認証トークンの発行を表すアノテーション。
 * このアノテーションが付与されているメソッド実行後に認証トークンの発行が行われる。
 */
@Inherited
@NameBinding
@Retention(RUNTIME)
@Target({ METHOD, TYPE })
public @interface GenerateToken {
}
