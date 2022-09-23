package io.extact.rms.platform.jwt;

import static io.extact.rms.platform.jwt.JsonWebTokenFilterTest.SecurityFilterTestApi.*;
import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.platform.jwt.JsonWebTokenFilterTest.TestApplication;
import io.extact.rms.platform.jwt.JsonWebTokenFilterTest.TestUserClaimsFactory;
import io.extact.rms.platform.jwt.consumer.Authenticated;
import io.extact.rms.platform.jwt.impl.jose4j.Jose4PrivateSecretedTokenValidator;
import io.extact.rms.platform.jwt.impl.jose4j.Jose4jJwtGenerator;
import io.extact.rms.platform.jwt.provider.GenerateToken;
import io.extact.rms.platform.jwt.provider.JsonWebTokenGenerator.UserClaims;
import io.extact.rms.platform.jwt.provider.JsonWebTokenGenerator.UserClaimsFactory;
import io.extact.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest(resetPerTest = true)
@AddConfig(key = "server.port", value = "7001")
@AddBean(TestApplication.class)
@AddBean(TestUserClaimsFactory.class)
@AddBean(Jose4jJwtGenerator.class)
@AddBean(Jose4PrivateSecretedTokenValidator.class)
@ExtendWith(JulToSLF4DelegateExtension.class)
public class JsonWebTokenFilterTest {

    private SecurityFilterTestApi endPoint;
    private static String authHeaderValue;

    // ----------------------------------------------------- lifecycle methods

    @BeforeEach
    void setup() throws Exception {
        this.endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(SecurityFilterTestApi.class);
    }

    @AfterEach
    void tearDown() {
        authHeaderValue = null;
    }

    // ----------------------------------------------------- test methods

    @Test
    void testJwtAuthSuccessSequence() {
        // 認証除外でレスポンスヘッダにJWTが設定される
        endPoint.login(SUCCESS);
        // 受け取ったJWTを認証ヘッダに設定して認証配下のパスにアクセスできること
        endPoint.secure(SUCCESS);
        // 受け取ったJWTを認証ヘッダに設定して認証除外のパスにもアクセスできること
        endPoint.unsecure(SUCCESS);
    }

    @Test
    void testJwtAuthComplexSequence() {

        // ログインが失敗してJWTが設定されない
        WebApplicationException actual = catchThrowableOfType(() ->
            endPoint.login(ERROR),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());

        // JWTがないので認証エラーになる
        actual = catchThrowableOfType(() ->
            endPoint.secure(SUCCESS),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(UNAUTHORIZED.getStatusCode());

        // 今度はログインが成功してJWTが設定される
        endPoint.login(SUCCESS);
        // 今度はJWTがあるのでエラーにならない
        endPoint.secure(SUCCESS);

        // エラーがあったアクセスの後もJWTがあれば問題なく成功する
        actual = catchThrowableOfType(() ->
            endPoint.login(ERROR),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(INTERNAL_SERVER_ERROR.getStatusCode());
        endPoint.secure(SUCCESS);
    }

    @Test
    @AddConfig(key = "jwt.filter.enable", value = "false")
    void testFilterOff() {
        endPoint.secure(SUCCESS);
    }

    // ----------------------------------------------------- test mock classes

    @Path("/test")
    @RegisterProvider(JwtSenderClientResponseFilter.class)
    public interface SecurityFilterTestApi {

        static final String SUCCESS = "success";
        static final String ERROR = "error";

        @GET
        @Path("/login")
        @Produces(MediaType.APPLICATION_JSON)
        TestUserClaims login(@QueryParam("pttn") String pttn);

        @GET
        @Path("/secure")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{authHeaderValue}")
        @Produces(MediaType.TEXT_PLAIN)
        String secure(@QueryParam("pttn") String pttn);

        @GET
        @Path("/unsecure")
        @ClientHeaderParam(name=HttpHeaders.AUTHORIZATION, value="{authHeaderValue}")
        @Produces(MediaType.TEXT_PLAIN)
        String unsecure(@QueryParam("pttn") String pttn);

        default String authHeaderValue() {
            return authHeaderValue;
        }
    }

    // Register by @AddBenan
    public static class TestApplication extends Application {
        // RequestFilterやResponseFilterなどのProviderは@AddBeanでは登録されないため
        // Application#getClassesを経由で登録している。またApplication#getClassesを
        // オーバーライドするとResourceクラスの自動登録も行われなくなるためResourceクラスも
        // 併せて登録する必要がある
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(
                        SecurityFilterTestResourceImpl.class,
                        JwtSecurityFilterFeature.class
                    );
        }
    }

    // Register by @AddBenan
    @Path("/test")
    public static class SecurityFilterTestResourceImpl implements SecurityFilterTestApi {

        @GenerateToken
        @Override
        public TestUserClaims login(String pttn) {
            if (pttn.equals(ERROR)) {
                throw new RuntimeException();
            }
            return new TestUserClaims();
        }

        @Authenticated
        @RolesAllowed("roleA")
        @Override
        public String secure(String pttn) {
            if (pttn.equals(ERROR)) {
                throw new RuntimeException();
            }
            return "success";
        }

        @Authenticated
        @Override
        public String unsecure(String pttn) {
            if (pttn.equals(ERROR)) {
                throw new RuntimeException();
            }
            return "success";
        }
    }

    // Register by @AddBenan
    public static class TestUserClaimsFactory implements UserClaimsFactory {
        @Override
        public boolean canNewInstanceFrom(Object obj) {
            return (obj instanceof TestUserClaims);
        }
        @Override
        public UserClaims newInstanceFrom(Object obj) {
            return (TestUserClaims) obj;
        }
    }

    // Register by @RegisterProvider
    public static class JwtSenderClientResponseFilter implements ClientResponseFilter {
        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
            if (!responseContext.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                System.out.println("Authorizationなし");
                return;
            }
            JsonWebTokenFilterTest.authHeaderValue = responseContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        }
    }

    // POJO
    public static class TestUserClaims implements UserClaims {
        @Override
        public String getUserId() {
            return "test";
        }
        @Override
        public String getUserPrincipalName() {
            return "test@test";
        }
        @Override
        public Set<String> getGroups() {
            return Set.of("roleA");
        }
    }
}
