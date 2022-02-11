package io.extact.rms.client.api.exception;

public class RmsSystemClientException extends RentalReservationClientException {

    public RmsSystemClientException(String message) {
        super(message);
    }

    public RmsSystemClientException(Throwable cause) {
        super(cause);
    }
}
