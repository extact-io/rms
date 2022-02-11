package io.extact.rms.application.exception;

/**
 * データ重複、該当データなしなどのユーザが意識する業務フローレベルのエラーを表す例外
 */
public class BusinessFlowException extends RentalReservationServiceException {

    private final CauseType causeType;

    public BusinessFlowException(String message, CauseType causeType, Throwable cause) {
        super(message, cause);
        this.causeType = causeType;
    }
    public BusinessFlowException(String message, CauseType causeType) {
        super(message);
        this.causeType = causeType;
    }
    public BusinessFlowException(CauseType causeType, Throwable cause) {
        super(cause);
        this.causeType = causeType;
    }

    public CauseType getCauseType() {
        return causeType;
    }

    public enum CauseType  {
        NOT_FOUND,
        DUPRICATE,
        FORBIDDEN,
        REFERED
    }
}
