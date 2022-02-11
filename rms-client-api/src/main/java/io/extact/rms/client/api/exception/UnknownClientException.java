package io.extact.rms.client.api.exception;

public class UnknownClientException extends RuntimeException {
    public UnknownClientException(Throwable cause) {
        super(cause);
    }
    public UnknownClientException(String message) {
        super(message);
    }
}
