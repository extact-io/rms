package io.extact.rms.external.webapi.mapper;

import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;

import io.extact.rms.application.exception.BusinessFlowException;
import io.extact.rms.application.exception.RmsSystemException;
import io.extact.rms.external.webapi.mapper.ValidationErrorInfo.ValidationErrorItem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerExceptionMappers {

    private static final String RMS_EXCEPTION_HEAD = "Rms-Exception";


    // ----------------------------------------------------- ExceptionMapper classes

    // Provider Class
    @Produces(MediaType.APPLICATION_JSON)
    public static class BusinessFlowExceptionMapper implements ExceptionMapper<BusinessFlowException> {

        @Override
        public Response toResponse(BusinessFlowException exception) {
            log.warn("exception occured. message={}", exception.getMessage());

            var errorInfo = new GenericErrorInfo(exception.getClass().getSimpleName(), exception.getMessage());

            Status status = switch (exception.getCauseType()) {
                case NOT_FOUND          -> Status.NOT_FOUND;
                case DUPRICATE, REFERED -> Status.CONFLICT;
                case FORBIDDEN          -> Status.FORBIDDEN;
            };

            return Response
                        .status(status)
                        .header(RMS_EXCEPTION_HEAD, BusinessFlowException.class.getSimpleName())
                        .entity(errorInfo)
                        .build();
        }
    }

    // Provider Class
    @Produces(MediaType.APPLICATION_JSON)
    public static class RmsSystemExceptionMapper implements ExceptionMapper<RmsSystemException> {

        @Override
        public Response toResponse(RmsSystemException exception) {
            log.warn("exception occured. message={}", exception.getMessage());
            var errorInfo = new GenericErrorInfo(exception.getClass().getSimpleName(), exception.getMessage());
            return Response
                        .status(Status.INTERNAL_SERVER_ERROR)
                        .header(RMS_EXCEPTION_HEAD, RmsSystemException.class.getSimpleName())
                        .entity(errorInfo)
                        .build();
        }
    }

    // Provider Class
    @Produces(MediaType.APPLICATION_JSON)
    public static class ConstraintExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

        @Override
        public Response toResponse(ConstraintViolationException exception) {
            log.warn("exception occured. message={}", exception.getMessage());

            Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
            List<ValidationErrorItem> errorItems = constraintViolations.stream()
                    .map(v -> new ValidationErrorItem(v.getPropertyPath().toString(), v.getMessage()))
                    .toList();

            var validationErrorInfo = new ValidationErrorInfo(
                    exception.getClass().getSimpleName(),
                    "validation error occurred.",
                    errorItems);

            return Response
                        .status(Response.Status.BAD_REQUEST)
                        .header(RMS_EXCEPTION_HEAD, exception.getClass().getSimpleName())
                        .entity(validationErrorInfo)
                        .build();
        }
    }
}
