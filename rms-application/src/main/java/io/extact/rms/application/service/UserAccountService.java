package io.extact.rms.application.service;

import java.util.function.Consumer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.extact.rms.application.domain.UserAccount;
import io.extact.rms.application.exception.BusinessFlowException;
import io.extact.rms.application.exception.BusinessFlowException.CauseType;
import io.extact.rms.application.persistence.GenericRepository;
import io.extact.rms.application.persistence.UserAccountRepository;

@ApplicationScoped
public class UserAccountService implements GenericService<UserAccount> {

    private UserAccountRepository repository;

    @Inject
    public UserAccountService(UserAccountRepository userRepository) {
        this.repository = userRepository;
    }

    public UserAccount findByLoginIdAndPasswod(String loginId, String password) {
        return repository.findByLoginIdAndPasswod(loginId, password);
    }


    public UserAccount findByLoginId(String loginId) {
        return repository.findByLoginId(loginId);
    }

    @Override
    public Consumer<UserAccount> getDuplicateChecker() {
        return (targetUser) -> {
            var foundUser = findByLoginId(targetUser.getLoginId());
            if (foundUser != null && (targetUser.getId() == null || !foundUser.isSameId(targetUser))) {
                throw new BusinessFlowException("loginId is already registered.", CauseType.DUPRICATE);
            }
        };
    }

    @Override
    public GenericRepository<UserAccount> getRepository() {
        return this.repository;
    }
}
