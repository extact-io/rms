package io.extact.rms.platform.extension;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;

/**
 * 設定によりCDI化されることを表すアノテーション。
 * 設定ファイルによりCDI登録するクラスは<code>@ApplicationScoped</code>などのCDIであることを
 * 明示的に示すアノテーションが付けないため、一見してCDIBeanかが分かりずらい。<br>
 * 設定によりCDI化されるクラスにはこのアノテーションを付けることでCDIBeanであることを分かり易く
 * する。<br>
 * なお、コードを分かりやすくすることのみが目的のアノテーションのため、実行時の動作にはなんら
 * 影響せず、このアノテーションが付いていなくても設定ファイルに登録されているクラスは問題なく
 * CDI化が行われる。
 *
 * @see ApplicationInitializerCdiExtension#registerConfiguredBeans(BeforeBeanDiscovery)
 */
@Documented
@Retention(SOURCE)
@Target({ TYPE })
public @interface ConfiguableScoped {
}
