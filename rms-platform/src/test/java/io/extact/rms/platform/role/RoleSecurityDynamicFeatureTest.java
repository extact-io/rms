package io.extact.rms.platform.role;

import static jakarta.ws.rs.core.Response.Status.*;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Set;

import jakarta.annotation.Priority;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.security.enterprise.CallerPrincipal;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest(resetPerTest = true)
@AddConfig(key = "server.port", value = "7001")
@ExtendWith(JulToSLF4DelegateExtension.class)
public class RoleSecurityDynamicFeatureTest {

    /**
     * メソッドごとに@DenyAllが指定されている場合のテスト
     */
    @Test
    @AddBean(TestApplication1.class)
    void testDenyAllMethods() throws Exception {

        DenyAllTestApi endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(DenyAllTestApi.class);

        // メソッドごとの@DenyAll指定の確認
        WebApplicationException actual = catchThrowableOfType(() ->
            endPoint.denyMethod1(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(FORBIDDEN.getStatusCode());
        actual = catchThrowableOfType(() ->
            endPoint.denyMethod2(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(FORBIDDEN.getStatusCode());
    }

    /**
     * メソッドごとに@RolesAllowedが指定されている場合のテスト
     */
    @Test
    @AddBean(TestApplication2.class)
    void testRolesAllowedMethods() throws Exception {

        RolesAllowedTestApi endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(RolesAllowedTestApi.class);

        // 指定されたロールあり
        endPoint.roleA();

        // 指定されたロールなし
        WebApplicationException actual = catchThrowableOfType(() ->
            endPoint.roleX(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(FORBIDDEN.getStatusCode());

        // 指定された全ロールあり
        endPoint.roleAandB();
    }

    /**
     * クラスに@DenyAllが指定されている場合のテスト
     */
    @Test
    @AddBean(TestApplication3.class)
    void testDenyAllClass() throws Exception {

        DenyAllTestApi endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(DenyAllTestApi.class);

        // クラスに指定された@DenyAllでアクセスNG
        WebApplicationException actual = catchThrowableOfType(() ->
            endPoint.denyMethod1(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(FORBIDDEN.getStatusCode());

        // メソッドに指定された@RolesAllowedが優先されOKとなる
        endPoint.denyMethod2();

        // メソッドに指定された@RolesAllowedが優先されるが該当のロールがないのでNGとなる
        actual = catchThrowableOfType(() ->
            endPoint.denyRolesAllowedOverridedMethod(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(FORBIDDEN.getStatusCode());
    }

    /**
     * クラスに@RolesAllowedが指定されている場合のテスト
     */
    @Test
    @AddBean(TestApplication4.class)
    void testRolesAllowedClass() throws Exception {

        RolesAllowedTestApi endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(RolesAllowedTestApi.class);

        // クラスに指定されたロールあり
        endPoint.roleA();

        // メソッドで指定された@RolesAllowedが優先され該当のロールがないためNG
        WebApplicationException actual = catchThrowableOfType(() ->
            endPoint.roleX(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(FORBIDDEN.getStatusCode());

        // メソッドで指定された@DenyAllが優先されNG
        actual = catchThrowableOfType(() ->
            endPoint.roleAandB(),
            WebApplicationException.class
        );
        assertThat(actual.getResponse().getStatus()).isEqualTo(FORBIDDEN.getStatusCode());
    }


    /**
     * filterチェックOFFの設定テスト
     */
    @Test
    @AddBean(TestApplication2.class)
    @AddConfig(key = "jwt.filter.enable", value = "false")
    void testFilterOff() throws Exception {

        RolesAllowedTestApi endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(RolesAllowedTestApi.class);

        // 全部エラーなく呼び出せること
        endPoint.roleA();
        endPoint.roleX();
        endPoint.roleAandB();
    }

    // ----------------------------------------------------- test client interface.

    // @DenyAllに対するテストインタフェース
    @Path("/test")
    public interface DenyAllTestApi {

        @GET
        @Path("/deny1")
        @Produces(MediaType.TEXT_PLAIN)
        String denyMethod1();

        @GET
        @Path("/deny2")
        @Produces(MediaType.TEXT_PLAIN)
        String denyMethod2();

        @GET
        @Path("/denyRolesAllowedOverrided")
        @Produces(MediaType.TEXT_PLAIN)
        String denyRolesAllowedOverridedMethod();
    }

    // @RolesAllowedに対するテストインタフェース
    @Path("/test")
    public interface RolesAllowedTestApi {

        @GET
        @Path("/roleA")
        @Produces(MediaType.TEXT_PLAIN)
        String roleA();

        @GET
        @Path("/roleX")
        @Produces(MediaType.TEXT_PLAIN)
        String roleX();

        @GET
        @Path("/roleAandB")
        @Produces(MediaType.TEXT_PLAIN)
        String roleAandB();
    }


    // ----------------------------------------------------- test resource classes.

    // ------------------------------------------
    // メソッドレベルのアノテーション動作を確認するテストクラス
    // ------------------------------------------
    // @DenyAllをメソッドごとに付与
    @Path("/test")
    public static class DenyAllToMethodResource implements DenyAllTestApi {

        @DenyAll
        @Override
        public String denyMethod1() {
            return "success";
        }

        @DenyAll
        @Override
        public String denyMethod2() {
            return "success";
        }

        @Override
        public String denyRolesAllowedOverridedMethod() {
            throw new UnsupportedOperationException();
        }
    }

    // @RolesAllowedをメソッドごとに付与
    @Path("/test")
    public static class RolesAllowedToMethodResource implements RolesAllowedTestApi {

        @RolesAllowed("roleA") // ロールあり
        @Override
        public String roleA() {
            return "success";
        }

        @RolesAllowed("roleX") // ロールなし
        @Override
        public String roleX() {
            return "success";
        }

        @RolesAllowed({"roleA", "roleB"}) // ロールあり
        @Override
        public String roleAandB() {
            return "success";
        }
    }

    // ------------------------------------------
    // クラスレベルのアノテーション動作を確認するテストクラス
    // ------------------------------------------
    // @DenyAllをクラスに付与
    @Path("/test")
    @DenyAll
    public static class DenyAllToClassResource implements DenyAllTestApi {

        // メソッドに指定がなくてもdenyとなること
        @Override
        public String denyMethod1() {
            return "success";
        }

        // メソッド指定が優先されアクセスOKとなること
        @RolesAllowed("roleA")
        @Override
        public String denyMethod2() {
            return "success";
        }

        // メソッド指定が優先されるがroleXの権限がないためNGとなること
        @RolesAllowed("roleX")
        @Override
        public String denyRolesAllowedOverridedMethod() {
            return "success";
        }
    }

    // @@RolesAllowedをクラスに付与
    @Path("/test")
    @RolesAllowed("roleA")
    public static class RolesAllowedToClassResource implements RolesAllowedTestApi {

        // メソッドの指定がないがアクセスOKとなること
        @Override
        public String roleA() {
            return "success";
        }

        // クラスで指定されたロールは持っているがメソッドで指定されたロールがないのでNGとなること
        @RolesAllowed("roleX")
        @Override
        public String roleX() {
            return "success";
        }

        // メソッド指定が優先されdenyとなること
        @DenyAll
        @Override
        public String roleAandB() {
            return "success";
        }
    }

    // Register by @AddBenan
    public static class TestApplication1 extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(
                        DenyAllToMethodResource.class,
                        RoleSecurityDynamicFeature.class,
                        TestSecurityContextRequestFilter.class
                    );
        }
    }
    public static class TestApplication2 extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(
                        RolesAllowedToMethodResource.class,
                        RoleSecurityDynamicFeature.class,
                        TestSecurityContextRequestFilter.class
                    );
        }
    }
    public static class TestApplication3 extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(
                        DenyAllToClassResource.class,
                        RoleSecurityDynamicFeature.class,
                        TestSecurityContextRequestFilter.class
                    );
        }
    }
    public static class TestApplication4 extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            return Set.of(
                        RolesAllowedToClassResource.class,
                        RoleSecurityDynamicFeature.class,
                        TestSecurityContextRequestFilter.class
                    );
        }
    }


    // ----------------------------------------------------- test mock classes

    public static class TestSecurityContext implements SecurityContext {

        private static Set<String> roles = Set.of("roleA", "roleB");

        @Override
        public Principal getUserPrincipal() {
            return new CallerPrincipal("test@test");
        }

        @Override
        public boolean isUserInRole(String role) {
            return roles.contains(role);
        }

        @Override
        public boolean isSecure() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getAuthenticationScheme() {
            throw new UnsupportedOperationException();
        }
    }

    @Priority(Priorities.AUTHENTICATION)
    public static class TestSecurityContextRequestFilter implements ContainerRequestFilter {
        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
            requestContext.setSecurityContext(new TestSecurityContext());
        }
    }
}
