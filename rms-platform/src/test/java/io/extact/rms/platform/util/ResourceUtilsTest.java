package io.extact.rms.platform.util;

import static io.extact.rms.platform.config.ResourcePathUnderConfigProvider.*;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ResourceUtilsTest {

    // Expected results of InFileSystem
    private static final List<String> IN_FS_RESULT_ALL = List.of(
                "mpconfig/a.properties",
                "mpconfig/b.yaml",
                "mpconfig/dummy.txt",
                "mpconfig/sub/c.properties"
            );

    // Expected results of InFileSystem with Filter
    private static final List<String> IN_FS_RESULT_FILTERED = List.of(
            "mpconfig/a.properties",
            "mpconfig/b.yaml",
            "mpconfig/sub/c.properties"
        );

    // InJar search directory(targets helidon-config.jar)
    private static final String IN_JAR_FIND_DIR = "META-INF/maven/io.helidon.config/helidon-config";

    // Expected results of InJar
    private static final List<String> IN_JAR_RESULT = List.of(
            IN_JAR_FIND_DIR + "/pom.properties",
            IN_JAR_FIND_DIR + "/pom.xml"
        );

    // Expected results of InJar with Filter
    private static final List<String> IN_JAR_RESULT_FILTERED = List.of(
            IN_JAR_FIND_DIR + "/pom.properties"
        );


    // ----------------------------------------------------- for InFileSystem TEST

    // The file in the specified directory can be read.
    @Test
    void testFindResoucePathUnderInFs() throws IOException {
        List<URL> urls = ResourceUtils.findResoucePathUnder("mpconfig");
        assertThat(urls).hasSize(IN_FS_RESULT_ALL.size());
        assertUrls(IN_FS_RESULT_ALL, urls);
    }

    // The file in the specified directory can be read + the filter works
    @Test
    void testFindResoucePathUnderInFsWithFilter() throws IOException {

        List<URL> urls = ResourceUtils.findResoucePathUnder(
                "mpconfig",
                SUPPROTED_CONFIG_FILTER,
                Thread.currentThread().getContextClassLoader());

        assertThat(urls).hasSize(IN_FS_RESULT_FILTERED.size());
        assertUrls(IN_FS_RESULT_FILTERED, urls);
    }

    // Can read the corresponding file even if you specify the file path instead of the directory.
    @Test
    void testFindResoucePathUnderInFsFileName() throws IOException {
        List<URL> urls = ResourceUtils.findResoucePathUnder("mpconfig/sub/c.properties");
        assertThat(urls).hasSize(1);
        assertUrls(List.of("mpconfig/sub/c.properties"), urls);
    }


    // ----------------------------------------------------- for InJar TEST

    // Same as InFS
    @Test
    void testFindResoucePathUnderInJar() throws IOException {
        List<URL> urls = ResourceUtils.findResoucePathUnder(IN_JAR_FIND_DIR);
        assertThat(urls).hasSize(IN_JAR_RESULT.size());
        assertUrls(IN_JAR_RESULT, urls);
    }

    // Same as InFS
    @Test
    void testFindResoucePathUnderInJarWithFilter() throws IOException {

        List<URL> urls = ResourceUtils.findResoucePathUnder(
                IN_JAR_FIND_DIR,
                SUPPROTED_CONFIG_FILTER,
                Thread.currentThread().getContextClassLoader());

        assertThat(urls).hasSize(IN_JAR_RESULT_FILTERED.size());
        assertUrls(IN_JAR_RESULT_FILTERED, urls);
    }

    // Same as InFS
    @Test
    void testFindResoucePathUnderInJarFileName() throws IOException {
        List<URL> urls = ResourceUtils.findResoucePathUnder(IN_JAR_FIND_DIR + "/pom.xml");
        assertThat(urls).hasSize(1);
        assertUrls(List.of(IN_JAR_FIND_DIR + "/pom.xml"), urls);
    }


    // ----------------------------------------------------- for Common TEST

    // When you specify an unknown path
    @Test
    void testFindResoucePathUnderUnknownDir() throws IOException {
        List<URL> urls = ResourceUtils.findResoucePathUnder("unknown");
        assertThat(urls).isEmpty();
    }


    // ----------------------------------------------------- private methods

    private void assertUrls(List<String> expecteds, List<URL> urls) {
        for (String suffix : expecteds) {
            boolean exist = urls.stream()
                        .map(URL::toString)
                        .anyMatch(url -> url.endsWith(suffix));
            if (!exist) {
                throw new IllegalStateException("not exist=>" + suffix);
            }
        }
    }
}
