package io.extact.rms.platform.debug;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InterceptorBinding;
import javax.interceptor.InvocationContext;

import org.eclipse.microprofile.config.Config;

import lombok.extern.slf4j.Slf4j;

import io.extact.rms.platform.debug.DebugSleepInterceptor.DebugSleep;

@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
@DebugSleep
@Slf4j
public class DebugSleepInterceptor {

    private boolean sleepEnable;
    private int sleepTime;

    @Inject
    public DebugSleepInterceptor(Config config) {
        this.sleepEnable = config.getOptionalValue("debug.sleep.enable", boolean.class).orElse(false);
        this.sleepTime = config.getOptionalValue("debug.sleep.time", int.class).orElse(0);
    }

    @AroundInvoke
    public Object obj(InvocationContext ic) throws Exception {
        if (sleepEnable) {
            log.info("start debug sleep[{}msec]......", this.sleepTime);
            Thread.sleep(sleepTime);
            log.info("end debug sleep.");
        }
        return ic.proceed();
    }

    @Inherited
    @InterceptorBinding
    @Retention(RUNTIME)
    @Target({ METHOD, TYPE })
    public @interface DebugSleep {

    }
}
