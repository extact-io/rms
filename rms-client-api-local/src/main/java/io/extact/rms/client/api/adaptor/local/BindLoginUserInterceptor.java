package io.extact.rms.client.api.adaptor.local;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import io.extact.rms.application.common.LoginUserUtils;
import io.extact.rms.application.common.ServiceLoginUser;
import io.extact.rms.client.api.adaptor.local.BindLoginUser.LoginAction;
import io.extact.rms.client.api.dto.UserAccountClientDto;

@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@BindLoginUser
public class BindLoginUserInterceptor {

    private ServiceLoginUser currentLoginUser;

    @AroundInvoke
    public Object obj(InvocationContext ic) throws Exception {
        if (ic.getMethod().isAnnotationPresent(LoginAction.class)) {
            return invokeWithKeepLoginUser(ic);
        }
        return invokeWithBindLoginUser(ic);
    }

    private Object invokeWithKeepLoginUser(InvocationContext ic) throws Exception {
        var result = ic.proceed();
        if (result instanceof UserAccountClientDto) {
            var userAccountDto = (UserAccountClientDto) result;
            currentLoginUser = ServiceLoginUser.of(userAccountDto.getId(), userAccountDto.getRoles());
        }
        return result;
    }

    private Object invokeWithBindLoginUser(InvocationContext ic) throws Exception {
        try {
            LoginUserUtils.set(currentLoginUser);
            return ic.proceed();
        } finally {
            LoginUserUtils.remove();
        }
    }
}
