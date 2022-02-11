package io.extact.rms.client.api.dto;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ReservationClientDto implements Convertable {

    private Integer id;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String note;
    private int rentalItemId;
    private int userAccountId;
    private RentalItemClientDto rentalItemDto;
    private UserAccountClientDto userAccountDto;

    public static ReservationClientDto ofTransient(LocalDateTime startDateTime, LocalDateTime endDateTime, String note, int rentalItemId, int userAccountId) {
        return of(null, startDateTime, endDateTime, note, rentalItemId, userAccountId, null, null);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
