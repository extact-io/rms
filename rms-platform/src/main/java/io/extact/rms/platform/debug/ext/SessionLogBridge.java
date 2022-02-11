package io.extact.rms.platform.debug.ext;

import org.eclipse.persistence.logging.SessionLog;
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
            case SessionLog.OFF:
                break;
            case SessionLog.SEVERE:
                log.error(message);
                break;
            case SessionLog.WARNING:
                log.warn(message);
                break;
            case SessionLog.INFO:
                log.info(message);
                break;
            case SessionLog.CONFIG:
            case SessionLog.FINE:
                log.debug(message);
                break;
            case SessionLog.FINER:
            case SessionLog.FINEST:
            case SessionLog.ALL:
            default:
                log.trace(message);
                break;
        }
    }
}
