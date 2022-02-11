package io.extact.rms.client.api.adaptor.remote.dto;

import lombok.Getter;

import io.extact.rms.client.api.dto.RentalItemClientDto;

@Getter
public class AddRentalItemRequestDto {

    private String serialNo;
    private String itemName;

    // ----------------------------------------------------- constructor methods

    public AddRentalItemRequestDto(RentalItemClientDto clientDto) {
        serialNo = clientDto.getSerialNo();
        itemName = clientDto.getItemName();
    }
}
