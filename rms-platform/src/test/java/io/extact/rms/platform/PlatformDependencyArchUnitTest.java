package io.extact.rms.platform;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "io.extact.rms", importOptions = ImportOption.DoNotIncludeTests.class)
public class PlatformDependencyArchUnitTest {

    // ---------------------------------------------------------------------
    // platformパッケージ内部の依存関係の定義
    // ---------------------------------------------------------------------

    /**
     * jose4jへの依存はimpl.jose4jパッケージのみの定義
     * <pre>
     * ・jose4jへはimpl.jose4jパッケージでしか依存していないこと
     * </pre>
     */
    @ArchTest
    static final ArchRule test_jose4jへの依存はimplパッケージのみの定義 =
            noClasses()
                .that()
                    .resideInAPackage("io.extact.rms..")
                    .and().resideOutsideOfPackage("io.extact.rms.platform.jwt.impl.jose4j..")
            .should()
                .dependOnClassesThat()
                    .resideInAnyPackage(
                        "org.jose4j.jwt.."
                        );
    /**
     * jwtパッケージの依存関係の定義
     * <pre>
     * ・jwtパッケージ直下のクラスとfilterパッケージはjwtの実装依存のimplパッケージに依存してないこと
     * </pre>
     */
    @ArchTest
    static final ArchRule test_jwtパッケージ内部の依存関係の定義 =
            noClasses()
                .that()
                    .resideInAPackage("io.extact.rms.platform.jwt")
                    .or().resideInAPackage("io.extact.rms.platform.jwt.filter..")
                .should()
                    .dependOnClassesThat()
                        .resideInAnyPackage(
                                "io.extact.rms.platform.jwt.impl.."
                                );
}
