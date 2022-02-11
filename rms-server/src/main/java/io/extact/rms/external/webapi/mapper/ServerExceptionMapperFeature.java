package io.extact.rms.external.webapi.mapper;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import io.extact.rms.external.webapi.mapper.ServerExceptionMappers.BusinessFlowExceptionMapper;
import io.extact.rms.external.webapi.mapper.ServerExceptionMappers.ConstraintExceptionMapper;
import io.extact.rms.external.webapi.mapper.ServerExceptionMappers.RmsSystemExceptionMapper;
import io.extact.rms.platform.jaxrs.mapper.PageNotFoundExceptionMapper;
import io.extact.rms.platform.jaxrs.mapper.UnhandledExceptionMapper;

public class ServerExceptionMapperFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(BusinessFlowExceptionMapper.class);
        context.register(RmsSystemExceptionMapper.class);
        context.register(ConstraintExceptionMapper.class);
        context.register(PageNotFoundExceptionMapper.class);
        context.register(UnhandledExceptionMapper.class);
        return true;
    }
}
