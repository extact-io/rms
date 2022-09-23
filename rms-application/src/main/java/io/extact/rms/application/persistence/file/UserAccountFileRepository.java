package io.extact.rms.application.persistence.file;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.extact.rms.application.domain.UserAccount;
import io.extact.rms.application.persistence.UserAccountRepository;
import io.extact.rms.application.persistence.GenericRepository.ApiType;
import io.extact.rms.application.persistence.file.converter.EntityArrayConverter;
import io.extact.rms.application.persistence.file.io.FileAccessor;
import io.extact.rms.platform.extension.EnabledIfRuntimeConfig;

@ApplicationScoped
@EnabledIfRuntimeConfig(propertyName = ApiType.PROP_NAME, value = ApiType.FILE)
public class UserAccountFileRepository extends AbstractFileRepository<UserAccount> implements UserAccountRepository {

    @Inject
    public UserAccountFileRepository(FileAccessor fileAccessor, EntityArrayConverter<UserAccount> converter) {
        super(fileAccessor, converter);
    }

    @Override
    public UserAccount findByLoginIdAndPasswod(String loginId, String password) {
        return load().stream()
                .filter(attributes -> attributes[1].equals(loginId))
                .filter(attributes -> attributes[2].equals(password))
                .map(this.getConverter()::toEntity)
                .findFirst()
                .orElse(null);
    }

    @Override
    public UserAccount findByLoginId(String loginId) {
        return load().stream()
                .filter(attributes -> attributes[1].equals(loginId))
                .map(this.getConverter()::toEntity)
                .findFirst()
                .orElse(null);
    }
}
