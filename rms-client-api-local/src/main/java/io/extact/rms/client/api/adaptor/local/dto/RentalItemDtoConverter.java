package io.extact.rms.client.api.adaptor.local.dto;

import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.client.api.dto.RentalItemClientDto;

public class RentalItemDtoConverter {

    public static RentalItemClientDto toDto(RentalItem rentalItem) {
        var dto = new RentalItemClientDto();
        dto.setId(rentalItem.getId());
        dto.setSerialNo(rentalItem.getSerialNo());
        dto.setItemName(rentalItem.getItemName());
        return dto;
    }

    public static RentalItem toEntity(RentalItemClientDto dto) {
        var rentalItem = new RentalItem();
        rentalItem.setId(dto.getId());
        rentalItem.setSerialNo(dto.getSerialNo());
        rentalItem.setItemName(dto.getItemName());
        return rentalItem;
    }
}
