package io.extact.rms.platform.role;

import java.io.IOException;
import java.util.stream.Stream;

import javax.annotation.Priority;
import javax.annotation.security.RolesAllowed;
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
public class RolesAllowedRequestFilter implements ContainerRequestFilter {

    private RolesAllowed roleAnnotation;

    public RolesAllowedRequestFilter(RolesAllowed annotation) {
        this.roleAnnotation = annotation;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        var isInRole = Stream.of(roleAnnotation.value())
                .anyMatch(requestContext.getSecurityContext()::isUserInRole);

        var userPrincipal = requestContext.getSecurityContext().getUserPrincipal();
        if(!isInRole) {
            log.info("@RolesAllowedに対する権限がありません。path={}, allowedRoles={}, userName={}",
                    requestContext.getUriInfo().getPath(),
                    roleAnnotation.value(),
                    userPrincipal != null ? userPrincipal.getName() : "null");
            requestContext.abortWith(Response.status(Status.FORBIDDEN).build());
        }
    }
}
