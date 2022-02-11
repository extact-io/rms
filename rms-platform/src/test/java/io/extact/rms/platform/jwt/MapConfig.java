package io.extact.rms.platform.jwt;

import static io.extact.rms.platform.jwt.JwtConfig.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.weld.exceptions.UnsupportedOperationException;

public class MapConfig implements Config {

    private Map<String, Object> configMap = new HashMap<>();

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
}
