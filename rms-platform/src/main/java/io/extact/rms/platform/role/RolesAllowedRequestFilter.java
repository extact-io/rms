package io.extact.rms.platform.role;

import java.io.IOException;
import java.util.stream.Stream;

import jakarta.annotation.Priority;
import jakarta.annotation.security.RolesAllowed;
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
