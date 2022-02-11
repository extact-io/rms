package io.extact.rms.external.webapi.mapper;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "パラメータチェックエラー情報")
@Getter
@Setter
@NoArgsConstructor // for JSON Deserialize
public class ValidationErrorInfo extends GenericErrorInfo {

    private List<ValidationErrorItem> errorItems;

    public ValidationErrorInfo(String errorReason, String errorMessage, List<ValidationErrorItem> errorItems) {
        super(errorReason, errorMessage);
        this.errorItems = errorItems;
    }


    // ----------------------------------------------------- inner classes

    @Schema(description = "1件ごとのチェックエラー情報")
    @Getter
    @Setter
    @NoArgsConstructor // for JSON Deserialize
    @AllArgsConstructor
    public static class ValidationErrorItem {

        @Schema(description = "エラーとなった項目")
        private String fieldName;

        @Schema(description = "エラーメッセージ")
        private String message;
    }
}

