package io.extact.rms.client.api.adaptor.remote.dto;

import java.time.LocalDateTime;

import lombok.Getter;

import io.extact.rms.client.api.dto.ReservationClientDto;

@Getter
public class AddReservationRequestDto {

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String note;
    private int rentalItemId;
    private int userAccountId;

    // ----------------------------------------------------- constructor methods

    public AddReservationRequestDto(ReservationClientDto clientDto) {
        startDateTime = clientDto.getStartDateTime();
        endDateTime = clientDto.getEndDateTime();
        note = clientDto.getNote();
        rentalItemId = clientDto.getRentalItemId();
        userAccountId = clientDto.getUserAccountId();
    }

}
