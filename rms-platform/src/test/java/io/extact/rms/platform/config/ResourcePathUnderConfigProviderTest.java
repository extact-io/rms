package io.extact.rms.platform.config;

import static org.assertj.core.api.Assertions.*;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class ResourcePathUnderConfigProviderTest {

    private static final String SECURITY_CONFIG_AES_PASSPHRASE = "security.config.aes.insecure-passphrase";

    @AfterEach
    void setup() {
        System.clearProperty(SECURITY_CONFIG_AES_PASSPHRASE);
    }

    /*
     * Verification point!
     * - under mpconfig have been read
     * - priority function works
     * - encryption function also works
     * - (passphrase is set in the system properties so as not to have side effects on other tests)
     */
    @Test
    void testGetConfigSources() {

        // Set a dummy in the SystemProperty(ordinal=400) and make sure it is overwritten
        // by the ConfigFile(ordinal=800) with the correct settings
        System.setProperty("security.config.require-encryption", "false"); // for ${GCM=...}
        System.setProperty(SECURITY_CONFIG_AES_PASSPHRASE, "xxxx"); // for ${GCM=...}

        Config config = buildConfig();

        assertThat(config.getValue("a", String.class)).isEqualTo("prop"); // from moconfig/a.properties
        assertThat(config.getValue("b", String.class)).isEqualTo("yaml"); // from moconfig/b.yaml
        assertThat(config.getValue("c", String.class)).isEqualTo("prop"); // from moconfig/sub/c.properties
        assertThat(config.getValue("bb", String.class)).isEqualTo("mamezou"); // from moconfig/b.yaml and decryption

        System.setProperty(SECURITY_CONFIG_AES_PASSPHRASE, "mamezou"); // for ${GCM=...}
    }
    private Config buildConfig() {
        return ConfigProviderResolver.instance()
                .getBuilder()
                .addDefaultSources()
                .addDiscoveredSources()
                .addDiscoveredConverters()
                .build();
    }
}
