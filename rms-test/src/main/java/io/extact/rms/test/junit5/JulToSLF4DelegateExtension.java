package io.extact.rms.test.junit5;

import java.util.logging.LogManager;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * {@link java.util.logging.Logger JUL}に対する出力をSLF4Jにデリゲートさせる
 * JUnitExtension。
 * HelidonはSLF4J経由ではなくJULを使っているためSLF4Jへログ出力を集約するために
 * この処理が必要となる。
 */
public class JulToSLF4DelegateExtension implements BeforeAllCallback {

    static {
        // java.util.loggingの出力をSLF4Jへdelegate
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        /*
         * Helidonを起動するHelidonJunitExtensionもBeforeAllCallback#beforeAllのフックポイントを使っている
         * HelidonのログをSLF4JへdelegateさせるにはHelidonが起動するまでにdelegate処理を実行する必要がある
         * がJUnit5が用意する一番最初のフックポイントはbeforeAllののため確実にHelidonより早く呼び出せるポ
         * イントがない。
         * このため、beforeAllのコールバックを待たずにクラスがロードされた時点でSLF4Jへのdelegate処理を行っ
         * ている。
         * よって、このメソッドはstatic initializerが呼び出されるためのダミーなのでなにも行っていない。
         */
    }
}
