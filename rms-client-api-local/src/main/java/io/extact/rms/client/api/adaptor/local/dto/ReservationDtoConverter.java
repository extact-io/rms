package io.extact.rms.client.api.adaptor.local.dto;

import io.extact.rms.application.domain.Reservation;
import io.extact.rms.client.api.dto.ReservationClientDto;

public class ReservationDtoConverter {

    public static ReservationClientDto toDto(Reservation reservation) {
        var dto = new ReservationClientDto();
        dto.setId(reservation.getId());
        dto.setStartDateTime(reservation.getStartDateTime());
        dto.setEndDateTime(reservation.getEndDateTime());
        dto.setNote(reservation.getNote());
        dto.setRentalItemId(reservation.getRentalItemId());
        dto.setUserAccountId(reservation.getUserAccountId());
        dto.setUserAccountDto(
                reservation.getUserAccount() != null
                    ? reservation.getUserAccount().transform(UserAccountDtoConverter::toDto)
                    : null);
        dto.setRentalItemDto(
                reservation.getRentalItem() != null
                    ? reservation.getRentalItem().transform(RentalItemDtoConverter::toDto)
                    : null);
        return dto;
    }

    public static Reservation toEntity(ReservationClientDto dto) {
        var reservation = new Reservation();
        reservation.setId(dto.getId());
        reservation.setStartDateTime(dto.getStartDateTime());
        reservation.setEndDateTime(dto.getEndDateTime());
        reservation.setNote(dto.getNote());
        reservation.setRentalItemId(dto.getRentalItemId());
        reservation.setUserAccountId(dto.getUserAccountId());
        reservation.setUserAccount(
                dto.getUserAccountDto() != null
                    ? dto.getUserAccountDto().to(UserAccountDtoConverter::toEntity)
                    : null);
        reservation.setRentalItem(
                dto.getRentalItemDto() != null
                    ? dto.getRentalItemDto().to(RentalItemDtoConverter::toEntity)
                    : null);
        return reservation;
    }
}
