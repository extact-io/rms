package io.extact.rms.client.api.exception;

import java.util.List;
import java.util.stream.Collectors;

public class ValidateClientException extends RentalReservationClientException {

    private final transient ValidationErrorMessage errorMessage;

    public ValidateClientException(ValidationErrorMessage errorMessage) {
        super("サーバ側でバリデーションエラーが発生しました");
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        String detailMessage = errorMessage.getErrorItems().stream()
                .map(t -> t.getFieldName() + ":" + t.getMessage())
                .collect(Collectors.joining(","));
        return super.getMessage() + System.lineSeparator() + detailMessage;
    }

    public static class ValidationErrorMessage {
        private String errorReason;
        private String errorMessage;
        private List<ValidationErrorItem> errorItems;

        public String getErrorReason() {
            return errorReason;
        }
        public void setErrorReason(String errorReason) {
            this.errorReason = errorReason;
        }
        public String getErrorMessage() {
            return errorMessage;
        }
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        public List<ValidationErrorItem> getErrorItems() {
            return errorItems;
        }
        public void setErrorItems(List<ValidationErrorItem> errorItems) {
            this.errorItems = errorItems;
        }
    }

    public static class ValidationErrorItem {
        private String fieldName;
        private String message;
        public String getFieldName() {
            return fieldName;
        }
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
