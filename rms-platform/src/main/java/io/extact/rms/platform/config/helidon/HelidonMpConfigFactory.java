package io.extact.rms.platform.config.helidon;

import java.net.URL;
import java.nio.file.Paths;

import org.eclipse.microprofile.config.spi.ConfigSource;

import io.helidon.config.mp.MpConfigSources;
import io.helidon.config.yaml.mp.YamlMpConfigSource;

import lombok.extern.slf4j.Slf4j;

import io.extact.rms.platform.config.MpConfigFactory;

@Slf4j
public class HelidonMpConfigFactory implements MpConfigFactory {

    @Override
    public ConfigSource createFromExternalPath(String path) {
        ConfigSource source = null;
        if (path.endsWith(".properties")) {
            source = MpConfigSources.create(Paths.get(path));
        }
        if (path.endsWith(".yaml")) {
            source = YamlMpConfigSource.create(Paths.get(path));
        }
        if (source == null) {
            throw new IllegalArgumentException("Unknown config file ->" + path);
        }
        log.info("load config from [{}]", path);
        return source;
    }

    @Override
    public ConfigSource createFromUrl(URL configUrl) {
        ConfigSource source = null;
        if (configUrl.toString().endsWith(".properties")) {
            source = MpConfigSources.create(configUrl);
        }
        if (configUrl.toString().endsWith(".yaml")) {
            source = YamlMpConfigSource.create(configUrl);
        }
        if (source == null) {
            throw new IllegalArgumentException("Unknown config file ->" + configUrl);
        }
        log.info("load config from [{}]", configUrl);
        return source;
    }
}
