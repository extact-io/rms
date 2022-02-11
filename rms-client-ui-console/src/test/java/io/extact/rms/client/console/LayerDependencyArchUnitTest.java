package io.extact.rms.client.console;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "io.extact.rms", importOptions = ImportOption.DoNotIncludeTests.class)
public class LayerDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // アプリケーションアーキテクチャ全体レベルの依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * 論理モジュールの定義
     * <pre>
     * ・clientモジュールはアプリケーション(core)を操作/実行するためのモジュールの集合
     * ・applicationモジュールはアプリケーションそのものを司るモジュールの集合でクライアントの方式に依らないもの
     * ・externalモジュールはアプリケーションを外部に公開/連携するためのモジュールの集合
     * ・platformモジュールは業務に依らないキーメカニズムの集合
     * </pre>
     * 依存関係の定義
     * <pre>
     * ・clientモジュールはどのモジュールからも依存されていないこと
     * ・externalモジュールはどのモジュールからも依存されていないこと
     * ・applicationモジュールはclientモジュールとexternalモジュールから依存されていること
     * ・platformモジュールはclient、external、applicationの3つのモジュールから依存されていること
     * </pre>
     */
    @ArchTest
    static final ArchRule test_論理モジュール間の依存関係の定義 = layeredArchitecture()
            .layer("client").definedBy("io.extact.rms.client..")
            .layer("external").definedBy("io.extact.rms.external..")
            .layer("application").definedBy("io.extact.rms.application..")
            .layer("platform").definedBy("io.extact.rms.platform..")

            .whereLayer("client").mayNotBeAccessedByAnyLayer()
            .whereLayer("external").mayNotBeAccessedByAnyLayer()
            .whereLayer("application").mayOnlyBeAccessedByLayers("client", "external")
            .whereLayer("platform").mayOnlyBeAccessedByLayers("client", "external", "application");

    /**
     * アプリケーションアーキテクチャのレイヤと依存関係の定義
     * <pre>
     * ・uiレイヤはどのレイヤからも依存されていないこと（uiレイヤは誰も使ってはダメ）
     * ・apiレイヤはuiレイヤからのみ依存から依存を許可（uiレイヤ以外は誰も使ってはダメ）
     * ・webapiレイヤはどのレイヤからも依存されていないこと（webapiレイヤは誰も使ってはダメ））
     * ・applicationレイヤはapiとwebapiレイヤからのみ依存を許可（applicationレイヤを使って良いのはapiとwebapiレイヤのみ）
     * ・serviceレイヤapplicaitonレイヤからのみ依存を許可（serviceレイヤを使って良いのはapplicationレイヤのみ）
     * ・persistenceレイヤはserviceレイヤからのみ依存を許可（persistenceレイヤを使って良いのはserviceレイヤのみ）
     * ・domianレイヤはuiとplatform以外のレイヤからのみ依存されていること（uiレイヤはapiレイヤを経由してapplicationを使うので直接使ってはダメ）
     * ・platformレイヤはすべてのレイヤから依存されていること
     * </pre>
     */
    @ArchTest
    static final ArchRule test_レイヤー間の依存関係の定義 = layeredArchitecture()
            .layer("ui").definedBy("io.extact.rms.client.console..")
            .layer("api").definedBy("io.extact.rms.client.api..")
            .layer("webapi").definedBy("io.extact.rms.external.webapi..")
            .layer("application").definedBy("io.extact.rms.application")
            .layer("service").definedBy("io.extact.rms.application.service..")
            .layer("persistence").definedBy("io.extact.rms.application.persistence..")
            .layer("domain").definedBy("io.extact.rms.application.domain..", "io.extact.rms.application.exception..")
            .layer("platform").definedBy("io.extact.rms.platform..")

            .whereLayer("ui").mayNotBeAccessedByAnyLayer()
            .whereLayer("api").mayOnlyBeAccessedByLayers("ui")
            .whereLayer("webapi").mayNotBeAccessedByAnyLayer()
            .whereLayer("application").mayOnlyBeAccessedByLayers("api", "webapi")
            .whereLayer("service").mayOnlyBeAccessedByLayers("application")
            .whereLayer("persistence").mayOnlyBeAccessedByLayers("service")
            .whereLayer("domain").mayOnlyBeAccessedByLayers("api", "webapi", "application", "service", "persistence")
            .whereLayer("platform").mayOnlyBeAccessedByLayers("ui", "api", "webapi", "application", "service", "persistence", "domain");

    /**
     * 物理モジュール(jar)間の依存関係の定義
     * <pre>
     * ・-ui-console.jarはどのjarも依存しないこと
     * ・-api.jarに依存してよいのは直接利用する-ui-console.jarと-api.jarを実現する-api-remote.jarと-api-local.jarの3つのみ
     * ・-api-local.jarと-api-remote.jarはどのjarも依存しないこと
     * ・-serevice-server.jarはどのjarも依存しないこと
     * ・-serevice.jarに依存してよいのは直接利用する-serevice-server.jarと-api-local.jarの2つのみ
     * ・-platform.jarはすべてのjarから依存されて良い
     * </pre>
     */
    @ArchTest
    static final ArchRule test_物理モジュール間の定義 = layeredArchitecture()
            .layer("-client-ui-console.jar").definedBy(
                    "io.extact.rms.client.console..")
            .layer("-client-api.jar").definedBy(
                    "io.extact.rms.client.api",
                    "io.extact.rms.client.api.dto..",
                    "io.extact.rms.client.api.exception..",
                    "io.extact.rms.client.api.login..")
            .layer("-client-api-local.jar").definedBy("io.extact.rms.client.api.adaptor.local..")
            .layer("-client-api-remote.jar").definedBy("io.extact.rms.client.api.adaptor.remote..")
            .layer("-service-server.jar").definedBy("io.extact.rms.external.webapi..")
            .layer("-service.jar").definedBy("io.extact.rms.application..")
            .layer("-platform.jar").definedBy("io.extact.rms.platform..")

            .whereLayer("-client-ui-console.jar").mayNotBeAccessedByAnyLayer()
            .whereLayer("-client-api.jar").mayOnlyBeAccessedByLayers("-client-ui-console.jar", "-client-api-local.jar", "-client-api-remote.jar")
            .whereLayer("-client-api-local.jar").mayNotBeAccessedByAnyLayer()
            .whereLayer("-client-api-remote.jar").mayNotBeAccessedByAnyLayer()
            .whereLayer("-service-server.jar").mayNotBeAccessedByAnyLayer()
            .whereLayer("-service.jar").mayOnlyBeAccessedByLayers("-client-api-local.jar", "-service-server.jar")
            .whereLayer("-platform.jar").mayOnlyBeAccessedByLayers("-client-ui-console.jar", "-client-api.jar", "-client-api-local.jar",
                    "-client-api-remote.jar", "-service-server.jar", "-service.jar");

    /**
     * アプリのコードで依存OKなライブラリの定義。spiパッケージを除き依存してよいのは以下のモノのみ
     * <pre>
     * ・アプリ自身のクラス(io.extact.rms..)
     * ・Apache Commons Lang(org.apache.commons.lang3..)
     * ・SLF4J(org.slf4j..)
     * ・MicroProfile API(org.eclipse.microprofile..)
     * ・JavaEE API(javax..)
     * ・JavaSE API(java..)
     * </pre>
     * エントリポイントとなるMainクラス以外はHelidon(io.helidon..)に直接依存しないこと
     */
    @ArchTest
    static final ArchRule test_アプリが依存してOKなライブラリの定義 =
            classes()
                .that()
                    .resideInAPackage("io.extact.rms..")
                    .and().haveSimpleNameNotEndingWith("Main")
                    .and().resideOutsideOfPackage("io.extact.rms.test..")
                    .and().resideOutsideOfPackage("io.extact.rms.client.console.ui..")
                    .and().resideOutsideOfPackage("..jose4j..")
                    .and().resideOutsideOfPackage("..debug.ext..")
                    .and().resideOutsideOfPackage("..config.helidon..")
                    .and().resideOutsideOfPackage("..application.persistence.file.io..")
                .should()
                    .onlyDependOnClassesThat(
                            resideInAnyPackage( // helidonへの直接依存はなし
                                "io.extact.rms..",
                                "org.apache.commons.lang3..",
                                "org.slf4j..",
                                "org.eclipse.microprofile..",
                                "javax..",
                                "java.."
                            )
                            // https://github.com/TNG/ArchUnit/issues/183 による配列型の個別追加
                            .or(type(int[].class))
                            .or(type(char[].class))
                    );

}
