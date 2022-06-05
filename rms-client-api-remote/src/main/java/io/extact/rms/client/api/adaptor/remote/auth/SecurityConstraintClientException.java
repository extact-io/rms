package io.extact.rms.client.api.adaptor.remote.auth;

import javax.ws.rs.core.Response;

import io.extact.rms.client.api.exception.RentalReservationClientException;

public class SecurityConstraintClientException extends RentalReservationClientException {

    private final transient Response response;

    public SecurityConstraintClientException(Response response) {
        super(getMessage(response));
        this.response = response;
    }

    public int getErrorStatus() {
        return response.getStatus();
    }

    private static String getMessage(Response response) {
        return switch (response.getStatus()) {
            case 401 -> "認証エラー";
            case 403 -> "認可エラー";
            default -> "不明のエラー";
        };
    }
}
