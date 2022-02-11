package io.extact.rms.external.webapi.mapper;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "エラー情報")
public class GenericErrorInfo {

    // ----------------------------------------------------- field information

    @Schema(description = "エラー理由として発生した例外のクラス名を設定", example = "例外クラス名")
    private String errorReason;

    @Schema(description = "発生した例外に設定されていたメッセージ", example = "例外のエラーメッセージ")
    private String errorMessage;

    // ----------------------------------------------------- constructor methods

    public GenericErrorInfo() {
        // NOP
    }

    public GenericErrorInfo(String errorReason, String errorMessage) {
        this.errorReason = errorReason;
        this.errorMessage = errorMessage;
    }

    // ----------------------------------------------------- getter/setter methods

    public String getErrorReason() {
        return errorReason;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
