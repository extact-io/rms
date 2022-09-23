package io.extact.rms.platform.role;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import lombok.extern.slf4j.Slf4j;

@Priority(Priorities.AUTHORIZATION)
@ConstrainedTo(RuntimeType.SERVER)
@Slf4j
public class DenyAllRequestFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        log.info("@DenyAllによりアクセスが禁止されています。path={}", requestContext.getUriInfo().getPath());
        requestContext.abortWith(Response.status(Status.FORBIDDEN).build());
    }
}
