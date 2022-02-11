package io.extact.rms.external.webapi.dto;

import java.time.LocalDateTime;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import io.extact.rms.application.domain.Reservation;
import io.extact.rms.application.domain.constraint.BeforeAfterDateTime;
import io.extact.rms.application.domain.constraint.Note;
import io.extact.rms.application.domain.constraint.ReserveEndDateTime;
import io.extact.rms.application.domain.constraint.ReserveStartDateTimeFuture;
import io.extact.rms.application.domain.constraint.RmsId;
import io.extact.rms.application.domain.constraint.BeforeAfterDateTime.BeforeAfterDateTimeValidatable;
import io.extact.rms.application.domain.constraint.ValidationGroups.Add;

@Schema(description = "予約登録用DTO")
@BeforeAfterDateTime
@Getter
@Setter
public class AddReservationDto implements BeforeAfterDateTimeValidatable {

    @Schema(required = true, ref = "#/components/schemas/localDateTime")
    @ReserveStartDateTimeFuture(groups = Add.class)
    private LocalDateTime startDateTime;

    @Schema(required = true, ref = "#/components/schemas/localDateTime")
    @ReserveEndDateTime
    private LocalDateTime endDateTime;

    @Schema(required = false)
    @Note
    private String note;

    @RmsId
    @Schema(required = true)
    private int rentalItemId;

    @RmsId
    @Schema(required = true)
    private int userAccountId;

    public Reservation toEntity() {
        return Reservation.ofTransient(startDateTime, endDateTime, note, rentalItemId, userAccountId);
    }
}
