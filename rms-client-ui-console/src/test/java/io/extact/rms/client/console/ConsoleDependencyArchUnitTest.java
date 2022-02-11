package io.extact.rms.client.console;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "io.extact.rms", importOptions = ImportOption.DoNotIncludeTests.class)
public class ConsoleDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // consoleパッケージ内部の依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * consoleパッケージ内のアプリのコードで依存OKなライブラリの定義。依存してよいのは以下のモノのみ
     * <pre>
     * ・アプリ自身のクラス(io.extact.rms..)
     * ・Apache Commons Lang(org.apache.commons.lang3..)
     * ・SLF4J(org.slf4j..)
     * ・MicroProfile Config(org.eclipse.microprofile.config..)
     * ・CDI API(javax.inject.., javax.context..)
     * ・JavaSE API(java..)
     * </pre>
     * エントリポイントとなるMainクラス以外はHelidon(io.helidon..)に直接依存しないこと
     */
    @ArchTest
    static final ArchRule test_アプリが依存してOKなライブラリの定義 =
            classes()
                .that()
                    .resideInAPackage("io.extact.rms.client.console..")
                    .and().haveSimpleNameNotEndingWith("Main")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage(
                                "io.extact.rms..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "org.eclipse.microprofile.config..",
                                "org.beryx.textio..",
                                "javax.inject..",  // part of CDI
                                "javax.enterprise.context..", // part of CDI
                                "javax.enterprise.event..", // part of CDI
                                "java.."
                            )
                            // https://github.com/TNG/ArchUnit/issues/183 による配列型の個別追加
                            .or(type(int[].class))
                            .or(type(char[].class))
                    );


    /**
     * consoleパッケージのapiパッケージの利用法の定義
     * <pre>
     * ・consoleパッケージからapiパッケージのlocal or remote実装に直接依存してないこと
     * （consoleパッケージはapi.RentalReservationClientApiを窓口として使う）
     * </pre>
     */
    @ArchTest
    static final ArchRule test_apiパッケージの利用法の定義 =
            noClasses()
                .that()
                    .resideInAPackage("io.extact.rms.client.console..")
                .should()
                    .dependOnClassesThat()
                        .resideInAnyPackage(
                                "io.extact.rms.client.api.local..",
                                "io.extact.rms.client.api.remote.."
                                );
}
