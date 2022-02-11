package io.extact.rms.client.api.exception;

public class RentalReservationClientException extends RuntimeException {

    public RentalReservationClientException(String message) {
        super(message);
    }

    public RentalReservationClientException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
