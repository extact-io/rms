package io.extact.rms.platform.env;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class EnvironmentTest {

    private static final String MAIN_MANIFEST_JAR_PROP = "main.manifest.jar";

    @AfterEach
    void teardown( ) {
        System.clearProperty(MAIN_MANIFEST_JAR_PROP);
        Environment.clear();
    }

    @Test
    void tetGetMainJarInfo() {

        System.setProperty(MAIN_MANIFEST_JAR_PROP, "environment-test-normal\\.jar$");

        MainJarInfo mainJarInfo1 = Environment.getMainJarInfo();
        MainJarInfo mainJarInfo2 = Environment.getMainJarInfo();

        assertThat(mainJarInfo1).isSameAs(mainJarInfo2);
    }

    @Test
    void testUnknowMainJarInfo() {

        // "main.manifest.jar" prop non.
        MainJarInfo mainJarInfo = Environment.getMainJarInfo();

        assertThat(mainJarInfo.getApplicationName()).isEqualTo("-");
        assertThat(mainJarInfo.getJarName()).isEqualTo("-");
        assertThat(mainJarInfo.getMainClassName()).isEqualTo("-");
        assertThat(mainJarInfo.getVersion()).isEqualTo("-");
        assertThat(mainJarInfo.getBuildtimeInfo()).isEqualTo("-");
    }
}
