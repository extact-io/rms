package io.extact.rms.platform.jwtrole;

import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.platform.jwt.JwtSecurityFilterFeature;
import io.extact.rms.platform.jwt.JsonWebTokenFilterTest.TestUserClaims;
import io.extact.rms.platform.jwt.JsonWebTokenFilterTest.TestUserClaimsFactory;
import io.extact.rms.platform.jwt.consumer.Authenticated;
import io.extact.rms.platform.jwt.impl.jose4j.Jose4PrivateSecretedTokenValidator;
import io.extact.rms.platform.jwt.impl.jose4j.Jose4jJwtGenerator;
import io.extact.rms.platform.jwt.provider.GenerateToken;
import io.extact.rms.platform.jwtrole.JwtRoleSecurityFilterIntegrationTest.JwtRoleTestApplication;
import io.extact.rms.platform.role.RoleSecurityDynamicFeature;
import io.extact.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
@AddConfig(key = "server.port", value = "7001")
@AddBean(JwtRoleTestApplication.class)
@AddBean(TestUserClaimsFactory.class)
@AddBean(Jose4jJwtGenerator.class)
@AddBean(Jose4PrivateSecretedTokenValidator.class)
@ExtendWith(JulToSLF4DelegateExtension.class)
public class JwtRoleSecurityFilterIntegrationTest {


    private static String authHeaderValue;

    @AfterEach
    void tearDown() {
        authHeaderValue = null;
    }

    // 未認証のテストシナリオ
    @Test
    void testUnAuthenticateScenario() throws Exception {

        UnAuthenticateScenarioTestApi endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(UnAuthenticateScenarioTestApi.class);

        // 未認証→なにも指定なし→OK
        endPoint.noRestriction();

        // 未認証→@Authenticated&@RolesAllowed →NG（認証エラー）
        WebApplicationException actual = catchThrowableOfType(() ->
            endPoint.authAndRolesAnnotated(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(UNAUTHORIZED.getStatusCode());

        // 未認証→@RolesAllowed →NG（認可エラー）
        actual = catchThrowableOfType(() ->
            endPoint.rolesAnnotatedOnly(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(FORBIDDEN.getStatusCode());
    }

    // 認証済のテストシナリオ
    @Test
    void testAuthenticatedScenario() throws Exception {

        AuthenticatedScenarioTestApi endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(AuthenticatedScenarioTestApi.class);

        // 認証済みへ
        endPoint.login();

        // 認証→@Authenticatedのみ→OK
        endPoint.authAnnotatedOnly();

        // 認証→@Authenticated&@DenyAll → NG
        WebApplicationException actual = catchThrowableOfType(() ->
            endPoint.authAndDenyAllAnnotated(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(FORBIDDEN.getStatusCode());

        // 認証→@Authenticated&@RolesAllowed(ロールあり) → OK
        endPoint.authAndRolesAllowedAnnotatedOk();
        actual = catchThrowableOfType(() ->
            endPoint.authAndRolesAllowedAnnotatedNg(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(FORBIDDEN.getStatusCode());
    }

    // 未認証のテストシナリオ
    @Path("/test1")
    @RegisterProvider(JwtRoleSenderClientResponseFilter.class)
    public interface UnAuthenticateScenarioTestApi {

        // 未認証→なにも指定なし→OK
        @GET
        @Path("/noRestriction")
        @Produces(MediaType.TEXT_PLAIN)
        String noRestriction();

        // 未認証→@Authenticated&@RolesAllowed →NG（認証エラー）
        @GET
        @Path("/authAndRolesAnnotated")
        @Produces(MediaType.TEXT_PLAIN)
        String authAndRolesAnnotated();

        // 未認証→@RolesAllowed →NG（認可エラー）
        @GET
        @Path("/rolesAnnotatedOnly")
        @Produces(MediaType.TEXT_PLAIN)
        String rolesAnnotatedOnly();

    }

    // 認証済のテストシナリオ
    @Path("/test2")
    @RegisterProvider(JwtRoleSenderClientResponseFilter.class)
    public interface AuthenticatedScenarioTestApi {

        // ログイン
        @GET
        @Path("/login")
        @Produces(MediaType.APPLICATION_JSON)
        TestUserClaims login();

        // 認証→@Authenticatedのみ→OK
        @GET
        @Path("/authAnnotatedOnly")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{authHeaderValue}")
        @Produces(MediaType.TEXT_PLAIN)
        String authAnnotatedOnly();

        // 認証→@Authenticated&@DenyAll → NG
        @GET
        @Path("/authAndDenyAllAnnotated")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{authHeaderValue}")
        @Produces(MediaType.TEXT_PLAIN)
        String authAndDenyAllAnnotated();

        // 認証→@Authenticated&@RolesAllowed(ロールあり) → OK
        @GET
        @Path("/authAndRolesAllowedAnnotatedOk")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{authHeaderValue}")
        @Produces(MediaType.TEXT_PLAIN)
        String authAndRolesAllowedAnnotatedOk();

        // 認証→@Authenticated&@RolesAllowed(ロールなし) → NG(認可エラー)
        @GET
        @Path("/authAndRolesAllowedAnnotatedNg")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{authHeaderValue}")
        @Produces(MediaType.TEXT_PLAIN)
        String authAndRolesAllowedAnnotatedNg();

        default String authHeaderValue() {
            return authHeaderValue;
        }

    }

    @Path("/test1")
    public static class UnAuthenticateScenarioTestResource implements UnAuthenticateScenarioTestApi {

        @Override
        public String noRestriction() {
            return "success";
        }

        @Authenticated
        @RolesAllowed("roleX")
        @Override
        public String authAndRolesAnnotated() {
            return "success";
        }

        @RolesAllowed("roleX")
        @Override
        public String rolesAnnotatedOnly() {
            return "success";
        }
    }

    public static class AuthenticatedScenarioTestResource implements AuthenticatedScenarioTestApi {

        @GenerateToken
        @Override
        public TestUserClaims login() {
            return new TestUserClaims();
        }

        @Authenticated
        @Override
        public String authAnnotatedOnly() {
            return "success";
        }

        @Authenticated
        @DenyAll
        @Override
        public String authAndDenyAllAnnotated() {
            return "success";
        }

        @Authenticated
        @RolesAllowed("roleA")
        @Override
        public String authAndRolesAllowedAnnotatedOk() {
            return "success";
        }

        @Authenticated
        @RolesAllowed("roleX")
        @Override
        public String authAndRolesAllowedAnnotatedNg() {
            return "success";
        }

    }

    // Register by @AddBenan
    public static class JwtRoleTestApplication extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(
                        UnAuthenticateScenarioTestResource.class,
                        AuthenticatedScenarioTestResource.class,
                        JwtSecurityFilterFeature.class,
                        RoleSecurityDynamicFeature.class
                    );
        }
    }

    // Register by @RegisterProvider
    public static class JwtRoleSenderClientResponseFilter implements ClientResponseFilter {

        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
            if (!responseContext.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                System.out.println("Authorizationなし");
                return;
            }
            JwtRoleSecurityFilterIntegrationTest.authHeaderValue = responseContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        }
    }
}
