package io.extact.rms.application.exception;

/**
 * RMSで捕捉、送出した{@link BusinessFlowException}と{@link RmsSystemException}を共通的に
 * ハンドルするための基底例外クラス
 */
public abstract class RentalReservationServiceException extends RuntimeException {

    protected RentalReservationServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    protected RentalReservationServiceException(String message) {
        super(message);
    }

    protected RentalReservationServiceException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
