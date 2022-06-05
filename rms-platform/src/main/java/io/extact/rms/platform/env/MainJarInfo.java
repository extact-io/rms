package io.extact.rms.platform.env;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.Config;

import io.extact.rms.platform.util.ResourceUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class MainJarInfo {

    private static final String UNKNOWN_INFORMATION = "-";
    private static final String MAIN_JAR_PROP = "main.manifest.jar";

    static final MainJarInfo UNKNOWN_INFO;
    static {
        var temp = new MainJarInfo();
        temp.applicationName = UNKNOWN_INFORMATION;
        temp.jarName = UNKNOWN_INFORMATION;
        temp.mainClassName = UNKNOWN_INFORMATION;
        temp.version = UNKNOWN_INFORMATION;
        temp.buildtimeInfo = UNKNOWN_INFORMATION;
        UNKNOWN_INFO = temp;
    }

    private String applicationName;
    private String jarName;
    private String mainClassName;
    private String version;
    private String buildtimeInfo;


    // ----------------------------------------------------- static methods

    static MainJarInfoBuilder builder() {
        return new MainJarInfoBuilder();
    }


    // ----------------------------------------------------- getter methods

    public String startupModuleInfo() {
        if (applicationName.equals(UNKNOWN_INFORMATION)) {
            return UNKNOWN_INFORMATION;
        }
        return applicationName + "/" + jarName + "/" + mainClassName;
    }


    // ----------------------------------------------------- inner class def

    static class MainJarInfoBuilder {

        MainJarInfo build(Config config) {
            var manifestUrl = findTargetManifestUrl(config);
            return buidFromManifestUrl(manifestUrl);
        }

        private URL findTargetManifestUrl(Config config) {
            String jarFindName = config.getOptionalValue(MAIN_JAR_PROP, String.class).orElse(null);
            if (jarFindName == null) {
                return null;
            }

            // find jar metainf
            Enumeration<URL> metainfResources;
            try {
                metainfResources = Thread.currentThread().getContextClassLoader().getResources("META-INF/MANIFEST.MF");
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }

            // filter by jar protocol and filename
            Predicate<Object> findJarMatcher = new FindJarMatcher(jarFindName);
            List<URL> metainfUrls = Collections.list(metainfResources).stream()
                                .filter(url -> url.getProtocol().equals("jar"))
                                .filter(url -> findJarMatcher.test(ResourceUtils.extractFilePathStringOfUrl(url)))
                                .toList();

            // resolve metainf resource url
            if (metainfUrls.isEmpty()) {
                log.warn("no matched jar or META-INF [{}]", jarFindName);
                return null;
            }
            if (metainfUrls.size() > 1) {
                String urlPaths = metainfUrls.stream().map(URL::toString).collect(Collectors.joining(System.lineSeparator()));
                log.warn("to many match jar or META-INF [{}]" + System.lineSeparator() + urlPaths, jarFindName);
                return null;
            }
            return metainfUrls.get(0);
        }

        private MainJarInfo buidFromManifestUrl(URL manifestUrl) {

            // check parameter
            if (manifestUrl == null) {
                return null;
            }

            // resolve jarName from jarfile path
            var jarFilePath = manifestUrl.toString().substring(0, manifestUrl.toString().indexOf('!'));
            var jarName = jarFilePath.substring(jarFilePath.lastIndexOf("/") + 1, jarFilePath.length());

            // resolve manifest
            Manifest manifest;
            try (InputStream is = manifestUrl.openStream()) {
                manifest = new Manifest(is);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }

            // resolve metainf prop
            String applicationName = getManifestValue(manifest, "Application-Name", UNKNOWN_INFORMATION);
            String version = getManifestValue(manifest, "Application-Version", UNKNOWN_INFORMATION);
            String mainClassName = getManifestValue(manifest, "Main-Class", UNKNOWN_INFORMATION);
            String buildtime = getManifestValue(manifest, "Build-Time", UNKNOWN_INFORMATION);

            // setup MainJarInfo
            var mainJarInfo = new MainJarInfo();
            mainJarInfo.applicationName = applicationName;
            mainJarInfo.jarName = jarName;
            mainJarInfo.mainClassName = mainClassName;
            mainJarInfo.version = version;
            mainJarInfo.buildtimeInfo = buildtime;

            return mainJarInfo;
        }

        private String getManifestValue(Manifest manifest, String key, String defaultValue) {
            String value = manifest.getMainAttributes().getValue(key);
            return  (value == null) ? defaultValue : value;
        }
    }


    // ----------------------------------------------------- inner class def

    static class FindJarMatcher implements Predicate<Object> {
        private Pattern pattern;
        private FindJarMatcher(String findJarName) {
            this.pattern = Pattern.compile(findJarName);
        }
        @Override
        public boolean test(Object input) {
            return pattern.matcher(input.toString()).find();
        }
    }
}
