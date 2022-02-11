package io.extact.rms.platform.jwt.provider;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.lang3.Range;

import lombok.extern.slf4j.Slf4j;

import io.extact.rms.platform.jwt.JwtConfig;
import io.extact.rms.platform.jwt.provider.JsonWebTokenGenerator.UserClaimsFactory;

@GenerateToken
@ConstrainedTo(RuntimeType.SERVER)
@Slf4j
public class JwtProvideResponseFilter implements ContainerResponseFilter {

    private static final Range<Integer> SUCCESS_STATUS = Range.between(200, 299);

    private UserClaimsFactory userClaimsFactory;
    private JsonWebTokenGenerator tokenGenerator;

    @Inject
    public JwtProvideResponseFilter(UserClaimsFactory factory, JsonWebTokenGenerator generator) {
        this.userClaimsFactory = factory;
        this.tokenGenerator = generator;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        // 例外が発生した場合はExecptionMapperでレスポンスが設定されているので
        // まずはHTTPステータスを見て成功か失敗かを判定
        if (!SUCCESS_STATUS.contains(responseContext.getStatus())) {
            return;
        }

        if (!responseContext.hasEntity()) {
            log.warn("Reponse body is not set.");
            return;
        }

        Object entity = responseContext.getEntity();
        if (!userClaimsFactory.canNewInstanceFrom(entity)) {
            log.warn("The instance of the body is unexpected. [class={}]", entity.getClass().getName());
            return;
        }

        // JwtTokenの生成
        var userClaims = userClaimsFactory.newInstanceFrom(entity);
        String jwtToken = tokenGenerator.generateToken(userClaims);
        log.info("Generated JWT-Token=>[{}]", jwtToken); // ホントはログに書いちゃダメだけどネ

        var headers = responseContext.getHeaders();
        headers.add("Access-Control-Expose-Headers", HttpHeaders.AUTHORIZATION);
        headers.add(HttpHeaders.AUTHORIZATION, JwtConfig.BEARER_MARK + " " + jwtToken);
    }
}
