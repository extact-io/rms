package io.extact.rms.application.persistence.file.converter;

import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.application.exception.RmsSystemException;

public class RentalItemArrayConverter implements EntityArrayConverter<RentalItem> {

    public static final RentalItemArrayConverter INSTANCE = new RentalItemArrayConverter();

    public RentalItem toEntity(String[] attributes) throws RmsSystemException {

        var id = Integer.parseInt(attributes[0]);
        var serialNo = attributes[1];
        var itemName = attributes[2];

        return RentalItem.of(id, serialNo, itemName);
    }

    public String[] toArray(RentalItem rentalItem) {

        var itemAttributes = new String[3];

        itemAttributes[0] = String.valueOf(rentalItem.getId());
        itemAttributes[1] = rentalItem.getSerialNo();
        itemAttributes[2] = rentalItem.getItemName();

        return itemAttributes;
    }
}
