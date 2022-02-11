package io.extact.rms.application.persistence.file;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.application.persistence.RentalItemRepository;
import io.extact.rms.application.persistence.GenericRepository.ApiType;
import io.extact.rms.application.persistence.file.converter.EntityArrayConverter;
import io.extact.rms.application.persistence.file.io.FileAccessor;
import io.extact.rms.platform.extension.EnabledIfRuntimeConfig;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.FILE)
public class RentalItemFileRepository extends AbstractFileRepository<RentalItem> implements RentalItemRepository {

    @Inject
    public RentalItemFileRepository(FileAccessor fileAccessor, EntityArrayConverter<RentalItem> converter) {
        super(fileAccessor, converter);
    }

    @Override
    public RentalItem findBySerialNo(String serialNo) {
        return this.load().stream()
                .filter(attributes -> attributes[1].equals(serialNo))
                .map(this.getConverter()::toEntity)
                .findFirst()
                .orElse(null);
    }
}
