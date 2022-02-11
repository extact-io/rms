package io.extact.rms.platform.env;

import static org.assertj.core.api.Assertions.*;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class MainJarInfoTest {

    private static final String MAIN_MANIFEST_JAR_PROP = "main.manifest.jar";

    @AfterEach
    void teardown( ) {
        System.clearProperty(MAIN_MANIFEST_JAR_PROP);
    }

    /*
     * !!NOTE!!
     * Eclipseでこのテストを実行する場合は/rms-platform/testdata/environment-test-normal.zipを
     * 外部jarとして追加する（mavenから実行する場合はpomに設定を入れているので特別な手順は不要）
     */
    @Test
    void tetGetMainJarInfo() {

        // *.jar is registered as .gitignore and cannot be uploaded, so the extension is zip.
        System.setProperty(MAIN_MANIFEST_JAR_PROP, "environment-test-normal\\.zip$");

        Config config = ConfigProvider.getConfig();
        MainJarInfo mainJarInfo = MainJarInfo.builder().build(config);

        assertThat(mainJarInfo.getApplicationName()).isEqualTo("RentalManagementSystem");
        assertThat(mainJarInfo.getJarName()).isEqualTo("environment-test-normal.zip");
        assertThat(mainJarInfo.getMainClassName()).isEqualTo("dummy.Dummy");
        assertThat(mainJarInfo.getVersion()).isEqualTo("0.0.1-SNAPSHOT");
        assertThat(mainJarInfo.getBuildtimeInfo()).isNotNull();

        assertThat(mainJarInfo.startupModuleInfo()).isEqualTo("RentalManagementSystem/environment-test-normal.zip/dummy.Dummy");
    }

    @Test
    void tetGetMainJarInfoNotFound() {

        System.setProperty(MAIN_MANIFEST_JAR_PROP, "dummy\\.jar$");

        Config config = ConfigProvider.getConfig();
        MainJarInfo mainJarInfo = MainJarInfo.builder().build(config);

        assertThat(mainJarInfo).isNull();
    }

    @Test
    void tetGetMainJarInfoNoProperty() {
        Config config = ConfigProvider.getConfig();
        MainJarInfo mainJarInfo = MainJarInfo.builder().build(config);
        assertThat(mainJarInfo).isNull();
    }

    @Test
    void tetGetMainJarInfoTooManyMatch() {

        System.setProperty(MAIN_MANIFEST_JAR_PROP, "jakarta");

        Config config = ConfigProvider.getConfig();
        MainJarInfo mainJarInfo = MainJarInfo.builder().build(config);
        assertThat(mainJarInfo).isNull();
    }

    @Test
    void tetGetMainJarInfoUnknownApplicationName() {

        System.setProperty(MAIN_MANIFEST_JAR_PROP, "jakarta\\.inject-api");

        Config config = ConfigProvider.getConfig();
        MainJarInfo mainJarInfo = MainJarInfo.builder().build(config);
        assertThat(mainJarInfo.startupModuleInfo()).isEqualTo("-");
    }
}
