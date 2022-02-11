package io.extact.rms.external.webapi.dto;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import io.extact.rms.application.domain.Reservation;
import io.extact.rms.application.domain.constraint.BeforeAfterDateTime;
import io.extact.rms.application.domain.constraint.Note;
import io.extact.rms.application.domain.constraint.ReserveEndDateTime;
import io.extact.rms.application.domain.constraint.ReserveStartDateTime;
import io.extact.rms.application.domain.constraint.RmsId;
import io.extact.rms.application.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidatable;

@Schema(description = "予約DTO")
@Getter
@Setter
@BeforeAfterDateTime(from = "利用開始日時", to = "利用終了日時")
public class ReservationResourceDto implements BeforeAfterDateTimeValidatable {

    @RmsId
    @Schema(required = true)
    private Integer id;

    @Schema(required = true, ref = "#/components/schemas/localDateTime")
    @ReserveStartDateTime
    private LocalDateTime startDateTime;

    @Schema(required = true, ref = "#/components/schemas/localDateTime")
    @ReserveEndDateTime
    private LocalDateTime endDateTime;

    @Note
    @Schema(required = false)
    private String note;

    @RmsId
    @Schema(required = true)
    private int rentalItemId;

    @RmsId
    @Schema(required = true)
    private int userAccountId;

    @Schema(required = false)
    private UserAccountResourceDto userAccountDto;

    @Schema(required = false)
    private RentalItemResourceDto rentalItemDto;

    public static ReservationResourceDto toDto(Reservation entity) {
        if (entity == null) {
            return null;
        }
        var dto = new ReservationResourceDto();
        dto.setId(entity.getId());
        dto.setStartDateTime(entity.getStartDateTime());
        dto.setEndDateTime(entity.getEndDateTime());
        dto.setNote(entity.getNote());
        dto.setRentalItemId(entity.getRentalItemId());
        dto.setUserAccountId(entity.getUserAccountId());
        if (entity.getRentalItem() != null) {
            dto.setRentalItemDto(entity.getRentalItem().transform(RentalItemResourceDto::toDto));
        }
        if (entity.getUserAccount() != null) {
            dto.setUserAccountDto(entity.getUserAccount().transform(UserAccountResourceDto::toDto));
        }
        return dto;
    }

    public Reservation toEntity() {
        var reservation = Reservation.of(id, startDateTime, endDateTime, note, rentalItemId, userAccountId);
        if (rentalItemDto != null) {
            reservation.setRentalItem(rentalItemDto.toEntity());
        }
        if (userAccountDto != null) {
            reservation.setUserAccount(userAccountDto.toEntity());
        }
        return reservation;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
