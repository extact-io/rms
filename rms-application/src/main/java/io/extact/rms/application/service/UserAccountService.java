package io.extact.rms.application.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
    public UserAccount add(UserAccount addUserAccount) throws BusinessFlowException {

        // ログイン名の重複チェック
        var existingUserAccount = this.findByLoginId(addUserAccount.getLoginId());
        if (existingUserAccount != null) {
            throw new BusinessFlowException("loginId is already registered.", CauseType.DUPRICATE);
        }

        // 登録
        repository.add(addUserAccount);
        return get(addUserAccount.getId());
    }

    @Override
    public GenericRepository<UserAccount> getRepository() {
        return this.repository;
    }
}
