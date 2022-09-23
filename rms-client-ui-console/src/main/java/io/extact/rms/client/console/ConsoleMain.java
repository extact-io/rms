package io.extact.rms.client.console;

import static io.extact.rms.client.console.ui.ClientConstants.*;

import java.util.logging.LogManager;

import jakarta.enterprise.inject.spi.CDI;

import org.slf4j.bridge.SLF4JBridgeHandler;

import lombok.extern.slf4j.Slf4j;

import io.extact.rms.client.console.ui.ScreenController;
import io.extact.rms.client.console.ui.textio.TextIoUtils;
import io.extact.rms.platform.env.Environment;

@Slf4j
public class ConsoleMain {
    private static final String START_UP_LOGO ="""
                ____    __  ___  _____
               / __ \\  /  |/  / / ___/
              / /_/ / / /|_/ /  \\__ \\
             / _, _/ / /  / /_ ___/ /
            /_/ |_(_)_/  /_/(_)____(_)
            """;

    public static void main(String[] args) throws Exception {

        // Fiddlerの設定
        // System.setProperty("http.proxyHost", "localhost");
        // System.setProperty("http.proxyPort", "8888");
        // System.setProperty("web-api/mp-rest/url", "http://pmr216n.primo.mamezou.com:7001");
        // System.err.println("★Fiddlerの設定が入ってるのでFiddler立ち上げてね！");
        // System.err.println("★あと宛先アドレスはlocalhostではなくFQCN指定の方に変えてね！");
        try {
            // java.util.loggingの出力をSLF4Jへdelegate
            LogManager.getLogManager().reset();
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();

            // CDIコンテナの起動
            io.helidon.microprofile.cdi.Main.main(args);

            startupLog();
            startupLogo();

            ScreenController controller = CDI.current().select(ScreenController.class).get();
            while (true) {
                try {
                    controller.start();
                    break;
                } catch (Exception e) {
                    log.error("Back to start..", e);
                    TextIoUtils.printErrorInformation(UNKNOWN_ERROR_INFORMATION);
                }
            }
            // swingコンソールはmainプロセスが残るためexitする
            System.exit(0);
        } catch (Throwable e) {
            log.error("error occured.", e);
            throw e;
        }
    }

    private static void startupLog() {
        var mainJarInfo = Environment.getMainJarInfo();
        log.info(System.lineSeparator() +
                "Startup-Module:" + mainJarInfo.startupModuleInfo() + System.lineSeparator() +
                "Version:" + mainJarInfo.getVersion() + System.lineSeparator() +
                "Build-Time:" + mainJarInfo.getBuildtimeInfo()
                );
    }

    private static void startupLogo() {
        TextIoUtils.println(START_UP_LOGO);
    }
}
