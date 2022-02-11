package io.extact.rms.client.console.ui;

import static io.extact.rms.client.console.ui.ClientConstants.*;

import io.extact.rms.client.api.dto.RentalItemClientDto;
import io.extact.rms.client.api.dto.ReservationClientDto;
import io.extact.rms.client.api.dto.UserAccountClientDto;

public interface DtoFormatter<T> {

    String format(T dto);

    static class RentalItemFormatter implements DtoFormatter<RentalItemClientDto> {
        @Override
        public String format(RentalItemClientDto dto) {
            return String.format("[%s]%s シリアル番号：%s",
                    dto.getId(),
                    dto.getItemName(),
                    dto.getSerialNo());
        }
    }

    static class ReservationFormatter implements DtoFormatter<ReservationClientDto> {
        @Override
        public String format(ReservationClientDto dto) {
            return String.format("[%s] %s - %s %s %s %s",
                    dto.getId(),
                    DATETIME_FORMAT.format(dto.getStartDateTime()),
                    DATETIME_FORMAT.format(dto.getEndDateTime()),
                    dto.getRentalItemDto().getItemName(),
                    dto.getUserAccountDto().getUserName(),
                    dto.getNote());
        }
    }

    static class UserAccountFormatter implements DtoFormatter<UserAccountClientDto> {
        @Override
        public String format(UserAccountClientDto dto) {
            return String.format("[%s] %s/%s %s %s",
                    dto.getId(),
                    dto.getLoginId(),
                    dto.getPassword(),
                    dto.getUserName(),
                    dto.getUserType());
        }
    }
}
