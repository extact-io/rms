package io.extact.rms.application.persistence;

import static io.extact.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.extact.rms.application.domain.UserAccount;
import io.extact.rms.application.domain.UserAccount.UserType;
import io.extact.rms.test.junit5.TransactionalForTest;

public abstract class AbstractUserAccountRepositoryTest {

    protected abstract UserAccountRepository repository();

    @Test
    void testGet() {
        var expect = UserAccount.of(3, "admin", "admin", "管理者", "050-1111-2222", "連絡先3", UserType.ADMIN);
        var actual = repository().get(3);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testGetNull() {
        var actual = repository().get(999); // 該当なし
        assertThat(actual).isNull();
    }

    @Test
    void testFindAll() {
        var expected = List.of(
                UserAccount.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER),
                UserAccount.of(2, "member2", "member2", "メンバー2", "080-1111-2222", "連絡先2", UserType.MEMBER),
                UserAccount.of(3, "admin", "admin", "管理者", "050-1111-2222", "連絡先3", UserType.ADMIN)
                );
        var actuals = repository().findAll();
        assertThatToString(actuals).containsExactlyElementsOf(expected);
    }

    @Test
    void testFindByUserIdAndPasswod() {
        var expect = UserAccount.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = repository().findByLoginIdAndPasswod("member1", "member1");
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFindNullByUserIdAndPasswod() {
        var actual = repository().findByLoginIdAndPasswod("member1", "999999"); // password誤り
        assertThat(actual).isNull();
    }

    @Test
    void testFindByLoginId() {
        var expect = UserAccount.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = repository().findByLoginId("member1");
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFindNullByLoginId() {
        var actual = repository().findByLoginId("999999"); // 該当なし
        assertThat(actual).isNull();
    }

    @Test
    @TransactionalForTest
    void testUpdate() {
        var expect = UserAccount.of(1, "update1", "update2", "update3", "999-1111-2222", "update4", UserType.ADMIN);
        var updateUser = repository().get(1);
        updateUser.setLoginId("update1");
        updateUser.setPassword("update2");
        updateUser.setUserName("update3");
        updateUser.setPhoneNumber("999-1111-2222");
        updateUser.setContact("update4");
        updateUser.setUserType(UserType.ADMIN);
        var actual = repository().update(updateUser);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    @TransactionalForTest
    void testDelete() {
        // 削除実行
        var deleteUserAccount = repository().get(2);
        repository().delete(deleteUserAccount);
        // 削除後ファイルの取得
        var rentalItems = this.repository().findAll();
        // 検証
        var expected = List.of(
                UserAccount.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER),
                UserAccount.of(3, "admin", "admin", "管理者", "050-1111-2222", "連絡先3", UserType.ADMIN));
        assertThatToString(rentalItems).containsExactlyElementsOf(expected);
    }

    @Test
    void testUpdateTargetNotExists() {
        var updateUser = UserAccount.of(9, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = repository().update(updateUser);
        assertThat(actual).isNull();
    }
}
