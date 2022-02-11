package io.extact.rms.application.persistence.file;

import java.io.IOException;

import io.extact.rms.application.exception.RmsSystemException;

/**
 * RMSで捕捉済みを意味するIO例外
 */
public class IoSystemException extends RmsSystemException {

    public IoSystemException(IOException cause) {
        super(cause);
    }

    public IoSystemException(String message, IOException cause) {
        super(message, cause);
    }
}
