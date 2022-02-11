package io.extact.rms.external.webapi.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.application.domain.constraint.ItemName;
import io.extact.rms.application.domain.constraint.RmsId;
import io.extact.rms.application.domain.constraint.SerialNo;

@Schema(description = "レンタル品DTO")
@Getter
@Setter
public class RentalItemResourceDto {

    @RmsId
    @Schema(required = true)
    private Integer id;

    @SerialNo
    @Schema(required = true)
    private String serialNo;

    @ItemName
    @Schema(required = false)
    private String itemName;

    public static RentalItemResourceDto toDto(RentalItem entity) {
        if (entity == null) {
            return null;
        }
        var dto = new RentalItemResourceDto();
        dto.setId(entity.getId());
        dto.setSerialNo(entity.getSerialNo());
        dto.setItemName(entity.getItemName());
        return dto;
    }

    public RentalItem toEntity() {
        return RentalItem.of(id, serialNo, itemName);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
