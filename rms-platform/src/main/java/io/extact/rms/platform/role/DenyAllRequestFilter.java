package io.extact.rms.platform.role;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Priorities;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
