package io.extact.rms.external.webapi.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import lombok.extern.slf4j.Slf4j;

import io.extact.rms.application.exception.BusinessFlowException;
import io.extact.rms.application.exception.RmsSystemException;
import io.extact.rms.external.webapi.mapper.ValidationErrorInfo.ValidationErrorItem;

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

            Status status;
            switch (exception.getCauseType()) {
                case NOT_FOUND:
                    status =  Status.NOT_FOUND;
                    break;
                case DUPRICATE:
                case REFERED:
                    status =  Status.CONFLICT;
                    break;
                case FORBIDDEN:
                    status =  Status.FORBIDDEN;
                    break;
                default:
                    throw new IllegalStateException("unknown causeType:" + exception.getCauseType(), exception);
            }

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
                    .collect(Collectors.toList());

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
