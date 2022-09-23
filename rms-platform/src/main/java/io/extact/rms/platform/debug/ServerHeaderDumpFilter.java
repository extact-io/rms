package io.extact.rms.platform.debug;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

import lombok.extern.slf4j.Slf4j;

@Priority(Priorities.AUTHENTICATION -100)
@Slf4j(topic = "ServerHeaderDump")
public class ServerHeaderDumpFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        var builder = new StringBuilder();
        builder.append(requestContext.getMethod()).append(" ")
            .append("/" + requestContext.getUriInfo().getPath()).append(System.lineSeparator());
        requestContext.getHeaders().forEach((key, values) ->
            builder.append(key + ":" + values + System.lineSeparator())
        );
        if (log.isDebugEnabled()) {
            log.debug("===> REQUEST" + System.lineSeparator() + builder.toString());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        var builder = new StringBuilder();
        builder.append(responseContext.getStatusInfo().getStatusCode())
            .append(" " + responseContext.getStatusInfo().getReasonPhrase()).append(System.lineSeparator());
        responseContext.getHeaders().forEach((key, values) ->
            builder.append(key + ":" + values + System.lineSeparator())
        );
        if (log.isDebugEnabled()) {
            log.debug("<=== RESPONSE" + System.lineSeparator() + builder.toString());
        }
    }
}
