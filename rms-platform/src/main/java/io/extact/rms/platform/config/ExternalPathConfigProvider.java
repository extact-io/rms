package io.extact.rms.platform.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

/**
 * Read the YAML file of the specified path from the outside.
 * There are two ways to specify the path from the outside, A and B, and
 * if both are set,A has priority.<br>
 * Also, since this class is instantiated by ServiceLoader, if you want to use it,
 * define this class in `META-INF/services/org.eclipse.microprofile.config.spi.ConfigSourceProvider`<br>
 * The path specification method is as follows.
 * <p>Path by system property</p>
 * <pre>
 * $ java -Dext.config.path=/path/to/file MainClass
 * <pre>
 * <p>Path by Environment variable</p>
 * <pre>
 * $ export EXT_CONFIG_PATH=/path/to/file
 * $ java MainClass
 * <pre>
 * If the path is not specified, nothing is done.
 */
public class ExternalPathConfigProvider implements ConfigSourceProvider {

    public static final String PATH_PROP_NAME = "ext.config.path";
    public static final String PATH_ENV_NAME = "EXT_CONFIG_PATH";

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {

        // get path (priority is system property -> environment variable)
        String path = System.getProperty(PATH_PROP_NAME, null);
        if (path == null) {
            path = System.getenv(PATH_ENV_NAME);
        }

        if (path == null) {
            return new ArrayList<>();
        }
        return List.of(
                    MpConfigFactory.newInstance().createFromExternalPath(path)
                );
    }
}
