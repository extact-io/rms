package io.extact.rms.platform.health;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.function.BiFunction;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class MemoryHealthCheck {

    private String livenessName;
    private String readinessName;
    private MemoryLivenessEvaluator evaluator;
    private MemoryMXBean mbean;

    @Inject
    public MemoryHealthCheck(
            @ConfigProperty(name="healthCheck.memoryLiveness.name") String livenessName,
            @ConfigProperty(name="healthCheck.memoryReadiness.name") String readinessName,
            @ConfigProperty(name="healthCheck.memoryLiveness.method") String defaultMethod,
            @ConfigProperty(name="healthCheck.memoryLiveness.threshold") long defaultThreshold
        ) {
        this.livenessName = livenessName;
        this.readinessName = readinessName;
        this.evaluator = MemoryLivenessEvaluator.of(defaultMethod, defaultThreshold);
        this.mbean = ManagementFactory.getMemoryMXBean();
    }

    @Produces
    @Liveness
    public HealthCheck checkLivenss() {
        return () -> {
            MemoryUsage memoryUsage = mbean.getHeapMemoryUsage();
            log.info("MemoryUsage:" + memoryUsage);
            return  HealthCheckResponse
                .named(livenessName)
                .withData("init", memoryUsage.getInit() / (1024 * 1024)) // MByte
                .withData("used", memoryUsage.getUsed() / (1024 * 1024)) // MByte
                .withData("max", memoryUsage.getMax() / (1024 * 1024))   // MByte
                .withData("method", evaluator.name())
                .withData("threshold", evaluator.threshold())
                .state(evaluator.liveness(memoryUsage))
                .build();
        };
    }

    @Produces
    @Readiness
    public HealthCheck checkReadiness() {
        // since no memory viewpoint, unconditionally returns up.
        return () -> HealthCheckResponse.named(readinessName).up().build();
    }

    // ----------------------------------------------------- observe method

    void resetEvaluator(@Observes MemoryLivenessEvaluator evaluator) {
        log.info("recieve event. event=" + evaluator);
        this.evaluator = evaluator;
    }


    // ----------------------------------------------------- inner classes

    interface MemoryLivenessEvaluator {

        // メモリ使用量が閾値以下であることの評価
        static final MemoryUsageFunction absoluteFunction =
                (memoryUsage, threshold) -> memoryUsage.getUsed() < threshold * 1024 * 1024;
        // メモリ使用率が閾値以下であることの評価
        static final MemoryUsageFunction relativeFunction =
                (memoryUsage, threshold) -> (memoryUsage.getUsed() / (double) memoryUsage.getMax()) * 100 < (double) threshold;

        String name();
        long threshold();
        boolean liveness(MemoryUsage memoryUsage);

        static MemoryLivenessEvaluator of(String method, long threshold) {
            return switch (method) {
                case "abs" -> new EvaluateHolder("abs", absoluteFunction, threshold);
                case "rel" -> new EvaluateHolder("rel", relativeFunction, threshold);
                default -> new EvaluateHolder("rel", relativeFunction, threshold);
            };
        }
    }

    static class EvaluateHolder implements MemoryLivenessEvaluator {

        String name;
        MemoryUsageFunction func;
        long threshold;

        EvaluateHolder(String name, MemoryUsageFunction func, long threshold) {
            this.name = name;
            this.func = func;
            this.threshold = threshold;
        }

        @Override
        public String name() {
            return name;
        }
        @Override
        public long threshold() {
            return threshold;
        }
        @Override
        public boolean liveness(MemoryUsage memoryUsage) {
            return func.evaluate(memoryUsage, threshold);
        }
    }

    interface MemoryUsageFunction extends BiFunction<MemoryUsage, Long, Boolean> {
        default boolean evaluate(MemoryUsage memoryUsage, long threshold) {
            return apply(memoryUsage, threshold);
        }
    }

}