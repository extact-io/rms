package io.extact.rms.platform.health;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;

import io.extact.rms.platform.health.MemoryHealthCheck.MemoryLivenessEvaluator;

@Path("/memeval")
@ApplicationScoped
@Slf4j
public class MemoryEvaluateResource {

    @Inject
    private Event<MemoryLivenessEvaluator> event;

    @GET
    @Tag(name = "SeverManagement API")
    @Operation(
        operationId = "resetEvaluateMethod",
        summary = "memory-health-check-livenessのチェック方法の変更",
        description = "/health/readyによるReadinessProbeのmemory-health-check-livenessのチェック方法を変更する")
    @Parameter(name = "method", description = "評価方法。abs:絶対評価, rel:相対評価", required = true,
        schema = @Schema(implementation = String.class))
    @Parameter(name = "val",  description = "閾値。絶対評価の場合は使用ヒープサイズ, 相対評価の場合はヒープの使用率、", required = true,
        schema = @Schema(implementation = Long.class))
    @APIResponse(
        responseCode = "200",
        description = "成功。常に\"accepted.\"を返す",
        content = @Content(mediaType = "application/text"))
    public String resetEvaluateMethod(@QueryParam("method") String method, @QueryParam("val") long val){
        log.info("reset memory evaluate method. methdo={}, val={}", method, val);
        event.fire(MemoryLivenessEvaluator.of(method, val));
        return "accepted.";
    }
}
