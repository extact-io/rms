package io.extact.rms.external.webapi;

import java.util.logging.LogManager;

import org.slf4j.bridge.SLF4JBridgeHandler;

import lombok.extern.slf4j.Slf4j;

import io.extact.rms.platform.env.Environment;

@Slf4j
public class WebApiMain {

    public static void main(String[] args) throws Exception {

        // java.util.loggingの出力をSLF4Jへdelegate
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        try {
            io.helidon.microprofile.cdi.Main.main(args);
        } catch (Exception e) {
            log.error("startup failed.", e);
            throw e;
        }

        startupLog();
    }

    private static void startupLog() {
        var mainJarInfo = Environment.getMainJarInfo();
        log.info("Main Jar Information=>" + System.lineSeparator() +
                "Startup-Module:" + mainJarInfo.startupModuleInfo() + System.lineSeparator() +
                "Version:" + mainJarInfo.getVersion() + System.lineSeparator() +
                "Build-Time:" + mainJarInfo.getBuildtimeInfo()
                );
    }
}
