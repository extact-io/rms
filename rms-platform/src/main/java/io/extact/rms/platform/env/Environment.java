package io.extact.rms.platform.env;

import org.eclipse.microprofile.config.ConfigProvider;

public class Environment {

    private static MainJarInfo mainJarInfo;

    public static synchronized MainJarInfo getMainJarInfo() {
        if (mainJarInfo == null) {
            var config = ConfigProvider.getConfig();
            mainJarInfo = MainJarInfo.builder().build(config);
        }
        return mainJarInfo == null ? MainJarInfo.UNKNOWN_INFO : mainJarInfo;
    }

    static synchronized void clear() { // for TEST
        mainJarInfo = null;
    }
}
