package io.extact.rms.platform.config;

import java.net.URL;

import org.eclipse.microprofile.config.spi.ConfigSource;

public interface MpConfigFactory {

    static final String MPCONFIG_FACTORY_CLASS_PROP = "mpconfig.factory.class";
    static final String DEFAULT_FACTORY_CLASSNAME = "io.extact.rms.platform.config.helidon.HelidonMpConfigFactory";

    /**
     * Since Config may be used before starting the CDI container, DI cannot be used,
     * so an instance is dynamically created from the class name.
     *
     * @return MpConfigFactory instance
     */
    static MpConfigFactory newInstance() {
        String factoryClassName = System.getProperty(MPCONFIG_FACTORY_CLASS_PROP, DEFAULT_FACTORY_CLASSNAME);
        try {
            return (MpConfigFactory) Class.forName(factoryClassName).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    ConfigSource createFromExternalPath(String path);
    ConfigSource createFromUrl(URL configUrl);
}
