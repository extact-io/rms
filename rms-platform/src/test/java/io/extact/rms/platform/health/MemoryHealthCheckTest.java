package io.extact.rms.platform.health;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.glassfish.jersey.ext.cdi1x.internal.CdiComponentProvider;
import org.glassfish.jersey.microprofile.restclient.RestClientExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.config.ConfigCdiExtension;
import io.helidon.microprofile.health.HealthCdiExtension;
import io.helidon.microprofile.server.JaxRsCdiExtension;
import io.helidon.microprofile.server.ServerCdiExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.AddExtension;
import io.helidon.microprofile.tests.junit5.DisableDiscovery;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest(resetPerTest = true)
@DisableDiscovery
@AddExtension(ServerCdiExtension.class)
@AddExtension(JaxRsCdiExtension.class)
@AddExtension(RestClientExtension.class)
@AddExtension(ConfigCdiExtension.class)
@AddExtension(CdiComponentProvider.class)
@ExtendWith(JulToSLF4DelegateExtension.class)
@AddConfig(key = "server.port", value = "7001")
// ---- following specific parts
@AddExtension(HealthCdiExtension.class)
@AddBean(MemoryHealthCheck.class)
@AddBean(MemoryEvaluateResource.class)
public class MemoryHealthCheckTest {

    private HealthEndPoint endPoint;

    @BeforeEach
    void setup() throws Exception {
        this.endPoint = RestClientBuilder.newBuilder()
                .baseUri(new URI("http://localhost:7001"))
                .build(HealthEndPoint.class);
    }

    public interface HealthEndPoint {

        @GET
        @Path("/health/live")
        @Produces(MediaType.APPLICATION_JSON)
        JsonObject getLiveness();

        @GET
        @Path("/health/ready")
        @Produces(MediaType.APPLICATION_JSON)
        JsonObject getReadiness();

        @GET
        @Path("/memeval")
        @Produces(MediaType.TEXT_PLAIN)
        String resetEvaluateMethod(@QueryParam("method") String method, @QueryParam("val") long val);
    }

    @Test
    void testMemoryLiveness() {
        JsonObject root = endPoint.getLiveness();

        // MemoryLiveness??????????????????????????????????????????
        JsonObject targetCheck = getTargetCheck(root, "memory-health-check-liveness");
        assertThat(targetCheck).isNotNull();

        // MemoryLiveness??????????????????????????????????????????
        JsonObject data = targetCheck.getJsonObject("data");
        assertThat(data).isNotNull().hasSize(5);
        assertThat(data.containsKey("init")).isTrue();
        assertThat(data.containsKey("max")).isTrue();
        assertThat(data.containsKey("method")).isTrue();
        assertThat(data.containsKey("threshold")).isTrue();
        assertThat(data.containsKey("used")).isTrue();
    }

    @Test
    void testMemoryReadiness() {
        JsonObject root = endPoint.getReadiness();

        // MemoryReadiness??????????????????????????????????????????
        JsonObject targetCheck = getTargetCheck(root, "memory-health-check-readiness");
        assertThat(targetCheck).isNotNull();

        // MemoryLiveness??????????????????????????????????????????
        assertThat(targetCheck.containsKey("data")).isFalse();
    }

    @Test
    @AddConfig(key = "healthCheck.memoryLiveness.method", value = "abs")
    @AddConfig(key = "healthCheck.memoryLiveness.threshold", value = "100000")
    void testResetEvaluateMethod() {

        JsonObject root = endPoint.getLiveness();

        // ??????????????????????????????
        JsonObject data = getTargetCheck(root, "memory-health-check-liveness").getJsonObject("data");
        assertThat(data.getString("method")).isEqualTo("abs");
        assertThat(data.getInt("threshold")).isEqualTo(100000);

        // reset???????????????????????????????????????
        endPoint.resetEvaluateMethod("rel", 99);
        root = endPoint.getLiveness();

        // ?????????????????????????????????
        data = getTargetCheck(root, "memory-health-check-liveness").getJsonObject("data");
        assertThat(data.getString("method")).isEqualTo("rel");
        assertThat(data.getInt("threshold")).isEqualTo(99);
    }

    private JsonObject getTargetCheck(JsonObject root, String targetName) {
        JsonArray checks = root.getJsonArray("checks");
        return (JsonObject) checks.stream()
                    .filter(check -> ((JsonObject) check).getString("name").equals(targetName))
                    .findFirst()
                    .orElse(null);
    }
}
