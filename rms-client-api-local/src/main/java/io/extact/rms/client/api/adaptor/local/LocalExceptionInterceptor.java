package io.extact.rms.client.api.adaptor.local;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@HandleExceptions
public class LocalExceptionInterceptor {

    @AroundInvoke
    public Object obj(InvocationContext ic) {
        Object result = null;
        try {
            result = ic.proceed();
        } catch (RuntimeException e) {
            LocalExceptionHandler.throwConvertedException(e);
        } catch (Exception e) {
            LocalExceptionHandler.throwConvertedException(e);
        }
        return result;
    }
}
