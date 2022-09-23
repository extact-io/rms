package io.extact.rms.external.webapi.jwt;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

import org.eclipse.microprofile.jwt.JsonWebToken;

import lombok.extern.slf4j.Slf4j;

import io.extact.rms.application.common.LoginUserUtils;
import io.extact.rms.application.common.ServiceLoginUser;
import io.extact.rms.platform.jwt.consumer.JwtValidateRequestFilter;

/**
 * 検証済み{@link JsonWebToken}から{@link ServiceLoginUser}を生成し<code>ThreadLocal</code>
 * に設定するフィルタークラス。
 * このフィルターは前段に{@link JwtValidateRequestFilter}が実行されていることを
 * 前提にしている。
 */
@Priority(Priorities.AUTHENTICATION + 10) // JwtSecurityRequestFilterの後
@ConstrainedTo(RuntimeType.SERVER)
@Slf4j
public class LoginUserRequestFilter implements ContainerRequestFilter, ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        var jsonWebToken = (JsonWebToken) requestContext.getSecurityContext().getUserPrincipal();
        ServiceLoginUser loginUser = ServiceLoginUser.UNKNOWN_USER;
        if (jsonWebToken != null) {
            loginUser = ServiceLoginUser.of(Integer.parseInt(jsonWebToken.getSubject()), jsonWebToken.getGroups());
        }
        log.debug("set loginUser to ThradLocal");
        LoginUserUtils.set(loginUser);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        log.debug("remove loginUser from ThradLocal");
        LoginUserUtils.remove();
    }
}
