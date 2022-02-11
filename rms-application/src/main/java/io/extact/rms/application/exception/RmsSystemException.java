package io.extact.rms.application.exception;

/**
 * RMSで捕捉済みを意味する実行時例外
 */
public class RmsSystemException extends RentalReservationServiceException {

    public RmsSystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public RmsSystemException(String message) {
        super(message);
    }

    public RmsSystemException(Throwable cause) {
        super(cause);
    }
}
