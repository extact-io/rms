package io.extact.rms.platform.jwt.consumer;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.jwt.JsonWebToken;

import lombok.extern.slf4j.Slf4j;

import io.extact.rms.platform.jwt.JwtConfig;
import io.extact.rms.platform.jwt.JwtValidateException;

@Priority(Priorities.AUTHENTICATION)
@ConstrainedTo(RuntimeType.SERVER)
@Authenticated
@Slf4j
public class JwtValidateRequestFilter implements ContainerRequestFilter {

    private JsonWebTokenValidator tokenValidator;
    private boolean enableFilter;

    @Inject
    public JwtValidateRequestFilter(JsonWebTokenValidator tokenValidator, Config config) {
        this.tokenValidator = tokenValidator;
        this.enableFilter = JwtConfig.of(config).enableFilter();
    }

    @Override
    public void filter(ContainerRequestContext request) throws IOException {

        // -- NOTE ----------
        // @Authenticatedがバインドされているメソッド以外では呼ばれないため
        // 認証が除外されているパスに対する考慮は不要
        // ------------------

        if (!enableFilter) {
            log.debug("設定(jwt.filter.enable)がOFFになっているため認証チェックは行いません");
            return;
        }

        // HeaderからBearerを取得
        var bearerString = request.getHeaderString(HttpHeaders.AUTHORIZATION);
        log.debug("bearer value=>[{}]", bearerString);
        if (bearerString == null || !bearerString.startsWith(JwtConfig.BEARER_MARK)) {
            log.warn("Authorizationヘッダの値が正しくありません。value=[{}]", bearerString);
            request.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        // Bearerを検証してPricipalオブジェクトに復号化
        JsonWebToken token;
        try {
            var bearerToken = bearerString.substring(7); // "Bearer xxxxxx"のxxxxxの部分を取得
            token = validateToken(bearerToken);
        } catch (JwtValidateException e) {
            log.warn(String.format("不正なトークンです。value=[{%s}]", bearerString), e);
            request.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        // 後続の処理で使えるようにSecurityContextを入れ替える
        var securityContext = request.getSecurityContext();
        var jwtSecurityContext = new JwtSecurityContext(
                    securityContext,
                    token,
                    "https".equalsIgnoreCase(request.getUriInfo().getAbsolutePath().getScheme())
                );
        request.setSecurityContext(jwtSecurityContext);
    }

    private JsonWebToken validateToken(String bearerToken) throws JwtValidateException {
        return tokenValidator.validate(bearerToken);
    }
}
