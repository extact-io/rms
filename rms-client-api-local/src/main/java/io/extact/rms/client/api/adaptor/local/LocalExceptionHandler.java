package io.extact.rms.client.api.adaptor.local;

import io.extact.rms.application.exception.BusinessFlowException;
import io.extact.rms.application.exception.RmsSystemException;
import io.extact.rms.client.api.exception.BusinessFlowClientException;
import io.extact.rms.client.api.exception.RmsSystemClientException;

public class LocalExceptionHandler {

    public static void throwConvertedException(RuntimeException e) {
        if (e instanceof RmsSystemException) {
            throw new RmsSystemClientException(e);
        }
        if (e instanceof BusinessFlowException) {
            throw new BusinessFlowClientException(e);
        }
        throw new RmsSystemClientException(e);
    }

    public static void throwConvertedException(Exception e) {
        throw new RmsSystemClientException(e);
    }
}
