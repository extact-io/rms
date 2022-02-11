package io.extact.rms.external.webapi.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.application.domain.constraint.ItemName;
import io.extact.rms.application.domain.constraint.SerialNo;

@Schema(description = "レンタル品登録用DTO")
@Getter
@Setter
public class AddRentalItemDto {

    @SerialNo
    @Schema(required = true)
    private String serialNo;

    @ItemName
    @Schema(required = false)
    private String itemName;

    public RentalItem toEntity() {
        return RentalItem.ofTransient(serialNo, itemName);
    }
}
