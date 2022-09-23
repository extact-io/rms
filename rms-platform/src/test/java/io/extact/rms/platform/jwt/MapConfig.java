package io.extact.rms.platform.jwt;

import static io.extact.rms.platform.jwt.JwtConfig.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import org.jboss.weld.exceptions.UnsupportedOperationException;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MapConfig implements Config {

    private Map<String, Object> configMap = new HashMap<>();

    // -------------------------------------------------------- implements Config.

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        if (!configMap.containsKey(propertyName)) {
            return null;
        }
        return (T) configMap.get(propertyName);
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        return Optional.ofNullable(getValue(propertyName, propertyType));
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return configMap.keySet();
    }
    @Override
    public Iterable<ConfigSource> getConfigSources() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        return new ConfigValueImpl(propertyName, configMap.get(propertyName).toString(),
                configMap.get(propertyName).toString(), this.toString(), 0);
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
        return Optional.empty();
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        if (type.equals(MapConfig.class)) {
            return type.cast(this);
        }
        if (type.equals(Config.class)) {
            return type.cast(this);
        }
        throw new UnsupportedOperationException("Cannot unwrap config into " + type.getName());
    }

    // -------------------------------------------------------- service methods.

    public void addThroghPath(String path) {
        long count = configMap.keySet().stream()
                    .filter(name -> name.startsWith(CONFIG_PREFIX + "passthrough"))
                    .count();
        configMap.put(CONFIG_PREFIX + "phrass." + count + ".path", path);
    }
    public void overwriteThroghPath(int pos, String path) {
        configMap.put(CONFIG_PREFIX + "phrass." + pos + ".path", path);
    }
    public void setSecretPhrase(String phrase) {
        configMap.put(CONFIG_PREFIX + "phrass", phrase);
    }
    public void setIssuer(String issuer) {
        configMap.put(CONFIG_PREFIX + "claim.issuer", issuer);
    }
    public void setIssuedAt(long val) {
        configMap.put(CONFIG_PREFIX + "claim.issuedAt", val);
    }
    public void setExpirationTime(Float exp) {
        configMap.put(CONFIG_PREFIX + "claim.exp", exp);
    }
    public void setAllowedClockSeconds(int seconds) {
        configMap.put(CONFIG_PREFIX + "claim.allowedClockSeconds", seconds);
    }

    // -------------------------------------------------------- inner class.

    @AllArgsConstructor
    @Getter
    static class ConfigValueImpl implements ConfigValue {
        private String name;
        private String value;
        private String rawValue;
        private String sourceName;
        private int sourceOrdinal;
    }
}