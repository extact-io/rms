package io.extact.rms.platform.jaxrs.converter;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

// register by @RegisterProvider or Application#getClasseses()
public class RmsTypeParameterFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(JsonbRmsConfig.class);
        context.register(ParamRmsConverterProvider.class);
        return true;
    }
}
