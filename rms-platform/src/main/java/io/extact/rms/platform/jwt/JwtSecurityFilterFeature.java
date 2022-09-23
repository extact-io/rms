package io.extact.rms.platform.jwt;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

import io.extact.rms.platform.jwt.consumer.JwtValidateRequestFilter;
import io.extact.rms.platform.jwt.provider.JwtProvideResponseFilter;

public class JwtSecurityFilterFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(JwtProvideResponseFilter.class);
        context.register(JwtValidateRequestFilter.class);
        return true;
    }
}
