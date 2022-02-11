package io.extact.rms.platform.config;


import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class ExternalPathConfigProviderTest {

    private static final String TEST_CONFIG_PROP = "test.config.value";

    @AfterEach
    void teardown() {
        System.clearProperty(MpConfigFactory.MPCONFIG_FACTORY_CLASS_PROP);
        System.clearProperty(ExternalPathConfigProvider.PATH_PROP_NAME);
        System.clearProperty(TEST_CONFIG_PROP);
    }

    @Test
    void testLoadPropertiesConfig() throws Exception {

        Path testConfPath = copyResourceToRealPath("testconf/normal.properties");
        System.setProperty(ExternalPathConfigProvider.PATH_PROP_NAME, testConfPath.toString());

        Config config = buildConfig();

        String actual= config.getValue(TEST_CONFIG_PROP, String.class);
        assertThat(actual).isEqualTo("prop");
    }

    @Test
    void testLoadYamlConfig() throws Exception {

        Path testConfPath = copyResourceToRealPath("testconf/normal.yaml");
        System.setProperty(ExternalPathConfigProvider.PATH_PROP_NAME, testConfPath.toString());

        Config config = buildConfig();

        String actual= config.getValue(TEST_CONFIG_PROP, String.class);
        assertThat(actual).isEqualTo("yaml");
    }

    @Test
    void testNoSsytemPropertyPath() {
        Config config = buildConfig();
        String actual= config.getOptionalValue(TEST_CONFIG_PROP, String.class).orElse(null);
        assertThat(actual).isNull();
    }

    @Test
    void testIncorrectSsytemPropertyPath() throws Exception {
        System.setProperty(ExternalPathConfigProvider.PATH_PROP_NAME, "unknown/path");
        catchThrowableOfType(
                () -> buildConfig(),
                IllegalArgumentException.class
            );
    }

    @Test
    void testIncorrectFactoryClass() throws Exception {
        System.setProperty(MpConfigFactory.MPCONFIG_FACTORY_CLASS_PROP, "unknown.class");
        System.setProperty(ExternalPathConfigProvider.PATH_PROP_NAME, "unknown/path");
        catchThrowableOfType(
                () -> buildConfig(),
                IllegalStateException.class
            );
    }

    @Test
    void testPriorityOfCustomConfig() throws Exception {

        System.setProperty(TEST_CONFIG_PROP, "system"); // ordinal:400

        Path testConfPath = copyResourceToRealPath("testconf/normal.properties"); // ordinal:50
        System.setProperty(ExternalPathConfigProvider.PATH_PROP_NAME, testConfPath.toString());

        Config config = buildConfig();

        String actual= config.getValue(TEST_CONFIG_PROP, String.class);
        assertThat(actual).isEqualTo("system");
    }

    @Test
    void testPriorityOfOverrideCustomConfig() throws Exception {

        System.setProperty(TEST_CONFIG_PROP, "system"); // ordinal:400

        Path testConfPath = copyResourceToRealPath("testconf/normal.yaml"); // ordinal:800
        System.setProperty(ExternalPathConfigProvider.PATH_PROP_NAME, testConfPath.toString());

        Config config = buildConfig();

        String actual= config.getValue(TEST_CONFIG_PROP, String.class);
        assertThat(actual).isEqualTo("yaml");
    }

    private Path copyResourceToRealPath(String testConfigResourcePath) throws IOException {

        // read testConf
        InputStream in = ExternalPathConfigProviderTest.class.getResourceAsStream("/" + testConfigResourcePath);

        // get the file name of testConf
        String[] resourcePathNodes = testConfigResourcePath.split("/");
        String testConfigFileName = resourcePathNodes[resourcePathNodes.length - 1];

        // create a temp directory and get the copy destination path
        Path tempFile = new TempDirPathResolver().resolve(testConfigFileName);

        // copy execution
        Files.copy(in, tempFile);
        return tempFile;
    }

    private Config buildConfig() {
        return ConfigProviderResolver.instance()
                .getBuilder()
                .addDefaultSources()
                .addDiscoveredSources()
                .addDiscoveredConverters()
                .build();
    }

    private static class TempDirPathResolver {
        private Path tempDir;
        public TempDirPathResolver() {
            try {
                this.tempDir = Files.createTempDirectory("rms_conf_");
                this.tempDir.toFile().deleteOnExit();
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
        }
        public Path resolve(String file) {
            Path tempFile = tempDir.resolve(file);
            tempFile.toFile().deleteOnExit();
            return tempFile;
        }
    }
}
