package io.extact.rms.platform.debug.ext;

import org.eclipse.persistence.platform.server.ServerLog;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "EclipseLink")
public class SessionLogBridge extends ServerLog {

    // EclipseLink側でDateTimeがappendされないようにする
    @Override
    public boolean shouldPrintDate() {
        return false;
    }

    @Override
    protected void basicLog(int level, String category, String message) {
        switch (level) {
            case OFF, SEVERE        -> log.error(message);
            case WARNING            -> log.warn(message);
            case INFO               -> log.info(message);
            case CONFIG, FINE       -> log.debug(message);
            case FINER, FINEST, ALL -> log.trace(message);
            default                 -> log.trace(message);
        };
    }
}
