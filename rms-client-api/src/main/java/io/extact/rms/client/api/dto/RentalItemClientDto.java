package io.extact.rms.client.api.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // for JSON Seserialize
@AllArgsConstructor(staticName = "of")
public class RentalItemClientDto implements Convertable {

    private Integer id;
    private String serialNo;
    private String itemName;

    public static RentalItemClientDto ofTransient(String serialNo, String itemName) {
        return of(null, serialNo, itemName);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
