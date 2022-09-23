package io.extact.rms.application.persistence.jpa;

import static io.extact.rms.test.assertj.ToStringAssert.*;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.application.TestUtils;
import io.extact.rms.application.domain.UserAccount;
import io.extact.rms.application.domain.UserAccount.UserType;
import io.extact.rms.application.persistence.AbstractUserAccountRepositoryTest;
import io.extact.rms.application.persistence.UserAccountRepository;
import io.extact.rms.test.junit5.JpaTransactionalExtension;
import io.extact.rms.test.junit5.TransactionalForTest;

@ExtendWith(JpaTransactionalExtension.class)
public class UserAccountJpaRepositoryTest extends AbstractUserAccountRepositoryTest {

    private UserAccountJpaRepository repository;

    @BeforeEach
    void setup(EntityManager em) {
        repository = new UserAccountJpaRepository();
        TestUtils.setFieldValue(repository, "em", em);
    }

    @Test
    @TransactionalForTest
    void testAdd() {
        var addEntity = UserAccount.ofTransient("member3", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER);
        repository.add(addEntity);

        addEntity.setId(4);
        var expect = addEntity;
        var actual = repository.get(4);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Override
    protected UserAccountRepository repository() {
        return repository;
    }
}
