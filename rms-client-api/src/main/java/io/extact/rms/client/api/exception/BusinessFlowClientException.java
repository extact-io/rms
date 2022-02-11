package io.extact.rms.client.api.exception;

public class BusinessFlowClientException extends RentalReservationClientException {

    public BusinessFlowClientException(String message) {
        super(message);
    }

    public BusinessFlowClientException(Throwable cause) {
        super(cause);
    }
}
