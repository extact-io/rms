package io.extact.rms.application.service;

import static io.extact.rms.application.TestUtils.*;
import static io.extact.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.application.TestUtils.PathResolverParameterExtension;
import io.extact.rms.application.domain.UserAccount;
import io.extact.rms.application.domain.UserAccount.UserType;
import io.extact.rms.application.persistence.file.io.PathResolver;

@ExtendWith(PathResolverParameterExtension.class)
class UserAccountServiceTest {

    private UserAccountService service;

    @BeforeEach
    void setup(PathResolver pathResolver) throws Exception {
        service = newUserAccountService(pathResolver);
    }

    @Test
    void testGet() {
        var expect = UserAccount.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = service.get(1);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testGetNull() {
        var actual = service.get(555); // 存在しないID
        assertThat(actual).isNull();
    }

    @Test
    void testFindAll() {
        var expected = List.of(
                UserAccount.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER),
                UserAccount.of(2, "member2", "member2", "メンバー2", "080-1111-2222", "連絡先2", UserType.MEMBER),
                UserAccount.of(3, "admin", "admin", "管理者", "050-1111-2222", "連絡先3", UserType.ADMIN)
                );
        var actuals = service.findAll();
        assertThatToString(actuals).containsExactlyElementsOf(expected);
    }

    @Test
    void testFindByLoginIdAndPassword() {
        var expect = UserAccount.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = service.findByLoginIdAndPasswod("member1", "member1");
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFindNullByIdAndPassword() {
        var actual = service.findByLoginIdAndPasswod("member1", "hoge"); // password誤り
        assertThat(actual).isNull();

        actual = service.findByLoginIdAndPasswod("hoge", "member1"); // loginId誤り
        assertThat(actual).isNull();

        actual = service.findByLoginIdAndPasswod("hoge", "hoge"); // 両方誤り
        assertThat(actual).isNull();
    }

    @Test
    void testFindByLoginId() {
        var expect = UserAccount.of(3, "admin", "admin", "管理者", "050-1111-2222", "連絡先3", UserType.ADMIN);
        UserAccount actual = service.findByLoginId("admin");
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFindNullLoginId() {
        UserAccount actual = service.findByLoginId("mamezou"); // 存在しないloginId
        assertThat(actual).isNull();
    }

    @Test
    void testAdd() {
        var addEntity = UserAccount.ofTransient("member3", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER);
        var expect = UserAccount.of(4, "member3", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER);
        UserAccount actual = service.add(addEntity);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testUpdate() {
        var expect = UserAccount.of(1, "member1", "UPDATE", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);

        var updateUser = service.get(1);
        updateUser.setPassword("UPDATE");

        var actual = service.update(updateUser);
        var reloadUser = service.get(1);

        assertThatToString(actual).isEqualTo(expect);
        assertThatToString(actual).isEqualTo(reloadUser);
    }
}
