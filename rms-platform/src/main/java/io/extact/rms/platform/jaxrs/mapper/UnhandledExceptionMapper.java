package io.extact.rms.platform.jaxrs.mapper;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Handle unhandled exceptions.
 */
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class UnhandledExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof WebApplicationException) {
            log.warn("Internal server error", exception);
            return ((WebApplicationException) exception).getResponse();
        } else {
            log.warn("Internal server error", exception);
            return Response.serverError().build();
        }
    }

}
