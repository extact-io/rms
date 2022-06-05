package io.extact.rms.client.api.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor // for JSON Seserialize
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
@ToString
public class RentalItemClientDto implements Convertable {

    private Integer id;
    private String serialNo;
    private String itemName;

    public static RentalItemClientDto ofTransient(String serialNo, String itemName) {
        return of(null, serialNo, itemName);
    }
}
