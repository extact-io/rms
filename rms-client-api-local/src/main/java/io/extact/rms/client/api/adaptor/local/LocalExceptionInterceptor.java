package io.extact.rms.client.api.adaptor.local;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

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
