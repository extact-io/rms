package io.extact.rms.platform.jwt;

import java.security.Key;
import java.util.function.Function;

import org.eclipse.microprofile.config.Config;

public class JwtConfig {

    public static final String CONFIG_PREFIX = "jwt.";
    public static final String BEARER_MARK = "Bearer";
    private static final long ISSUED_AT_NOW = -1;

    private Config config;

    public static JwtConfig of(Config config) {
        return new JwtConfig(config);
    }

    JwtConfig(Config config) {
        this.config = config;
    }

    public String getSecretPhrase() {
        return config.getValue(CONFIG_PREFIX + "phrass", String.class);
    }
    public Key getSecretKey(Function<String, Key> keyCreator) {
        return keyCreator.apply(getSecretPhrase());
    }
    public String getIssuer() {
        return config.getValue(CONFIG_PREFIX + "claim.issuer", String.class);
    }
    public boolean isIssuedAtToNow() {
        return getIssuedAt() == ISSUED_AT_NOW;
    }
    public long getIssuedAt() {
        return config.getValue(CONFIG_PREFIX + "claim.issuedAt", Long.class);
    }
    public float getExpirationTime() {
        return config.getValue(CONFIG_PREFIX + "claim.exp", Float.class);
    }
    public int getAllowedClockSeconds() {
        return config.getValue(CONFIG_PREFIX + "claim.allowedClockSeconds", Integer.class);
    }
    public boolean enableFilter() {
        return config.getValue(CONFIG_PREFIX + "filter.enable", Boolean.class);
    }
}
