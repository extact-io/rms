package io.extact.rms.platform.config;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import io.extact.rms.platform.util.ResourceUtils;

/**
 * Provider class that automatically reads the conf file under `mpconfig` path directly under the classpath.
 * If there is a subfolder, the subfolder is also recursively traced and read.<br>
 * Also, since this class is instantiated by ServiceLoader, if you want to use it,
 * define this class in `META-INF/services/org.eclipse.microprofile.config.spi.ConfigSourceProvider`<br>
 */
public class ResourcePathUnderConfigProvider implements ConfigSourceProvider {

    public static final String CONFIG_RESOURCE_PATH = "mpconfig";
    public static final List<String> SUPPROTED_CONFIG_TYPE = List.of(".properties", "yaml");
    public static final Predicate<Object> SUPPROTED_CONFIG_FILTER = (fileName) -> SUPPROTED_CONFIG_TYPE.stream()
            .anyMatch(type -> fileName.toString().endsWith(type));

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader classLoader) {
        List<URL> configUrls;
        try {
            configUrls = ResourceUtils.findResoucePathUnder(CONFIG_RESOURCE_PATH, SUPPROTED_CONFIG_FILTER, classLoader);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return configUrls.stream()
                    .map(url -> MpConfigFactory.newInstance().createFromUrl(url))
                    .collect(Collectors.toList());
    }
}
