package io.extact.rms.application.domain;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.Set;

import jakarta.validation.ConstraintViolation;

import org.junit.jupiter.api.Test;

import io.extact.rms.application.domain.UserAccount.UserType;
import io.extact.rms.application.domain.constraint.ValidationGroups.Update;
import io.extact.rms.test.assertj.ConstraintViolationSetAssert;

class UserAccountTest extends PropertyTest {

    @Override
    protected Class<?> getTargetClass() {
        return UserAccount.class;
    }

    @Test
    void testSetId() throws Exception {
        UserAccount testee = new UserAccount();
        testee.setId(100);
        Field id = this.getField("id");

        assertThat(id).isNotNull();
        assertThat(id.get(testee)).isEqualTo(100);
    }

    @Test
    void testGetId() throws Exception {
        UserAccount testee = new UserAccount();
        Field id = this.getField("id");

        assertThat(id).isNotNull();

        id.set(testee, 100);
        assertThat(testee.getId()).isEqualTo(100);
    }

    @Test
    void testSetLoginId() throws Exception {
        UserAccount testee = new UserAccount();
        testee.setLoginId("soramame");
        Field loginId = this.getField("loginId");

        assertThat(loginId).isNotNull();
        assertThat(loginId.get(testee)).isEqualTo("soramame");
    }

    @Test
    void testGetLoginName() throws Exception {
        UserAccount testee = new UserAccount();
        Field loginId = this.getField("loginId");

        assertThat(loginId).isNotNull();

        loginId.set(testee, "soramame");
        assertThat(testee.getLoginId()).isEqualTo("soramame");
    }

    @Test
    void testSetPassword() throws Exception {
        UserAccount testee = new UserAccount();
        testee.setPassword("soramame");
        Field password = this.getField("password");

        assertThat(password).isNotNull();
        assertThat(password.get(testee)).isEqualTo("soramame");
    }

    @Test
    void testGetPassword() throws Exception {
        UserAccount testee = new UserAccount();
        Field password = this.getField("password");

        assertThat(password).isNotNull();

        password.set(testee, "soramame");
        assertThat(testee.getPassword()).isEqualTo("soramame");
    }

    @Test
    void testSetUserName() throws Exception {
        UserAccount testee = new UserAccount();
        testee.setUserName("soramame");
        Field userName = this.getField("userName");

        assertThat(userName).isNotNull();
        assertThat(userName.get(testee)).isEqualTo("soramame");
    }

    @Test
    void testGetUserName() throws Exception {
        UserAccount testee = new UserAccount();
        Field userName = this.getField("userName");

        assertThat(userName).isNotNull();

        userName.set(testee, "soramame");
        assertThat(testee.getUserName()).isEqualTo("soramame");
    }

    @Test
    void testSetPhoneNumber() throws Exception {
        UserAccount testee = new UserAccount();
        testee.setPhoneNumber("1234567890");
        Field phoneNumber = this.getField("phoneNumber");

        assertThat(phoneNumber).isNotNull();
        assertThat(phoneNumber.get(testee)).isEqualTo("1234567890");
    }

    @Test
    void testGetPhoneNumber() throws Exception {
        UserAccount testee = new UserAccount();
        Field phoneNumber = this.getField("phoneNumber");

        assertThat(phoneNumber).isNotNull();

        phoneNumber.set(testee, "12345678890");
        assertThat(testee.getPhoneNumber()).isEqualTo("12345678890");
    }

    @Test
    void testSetContact() throws Exception {
        UserAccount testee = new UserAccount();
        testee.setContact("住所");
        Field contact = this.getField("contact");

        assertThat(contact).isNotNull();
        assertThat(contact.get(testee)).isEqualTo("住所");
    }

    @Test
    void testGetContact() throws Exception {
        UserAccount testee = new UserAccount();
        Field contact = this.getField("contact");

        assertThat(contact).isNotNull();

        contact.set(testee, "住所");
        assertThat(testee.getContact()).isEqualTo("住所");
    }

    @Test
    void testSetUserType() throws Exception {
        UserAccount testee = new UserAccount();
        testee.setUserType(UserType.ADMIN);
        Field userType = this.getField("userType");

        assertThat(userType).isNotNull();
        assertThat(userType.get(testee)).isEqualTo(UserType.ADMIN);
    }

    @Test
    void testGetUserType() throws Exception {
        UserAccount testee = new UserAccount();
        Field userType = this.getField("userType");

        assertThat(userType).isNotNull();

        userType.set(testee, UserType.ADMIN);
        assertThat(testee.getUserType()).isEqualTo(UserType.ADMIN);
    }

    @Test
    void testSetAdmin() throws Exception {
        UserAccount testee = new UserAccount();
        testee.setAdmin(true);
        assertThat(testee.getUserType()).isEqualTo(UserType.ADMIN);
        assertThat(testee.isAdmin()).isTrue();

        testee = new UserAccount();
        testee.setAdmin(false);
        assertThat(testee.getUserType()).isEqualTo(UserType.MEMBER);
        assertThat(testee.isAdmin()).isFalse();
    }

    @Test
    void testNewInstance() {
        UserAccount testee = UserAccount.of(1, "soramame", "password", "edamame", "12345", "address", UserType.MEMBER);

        assertThat(testee.getId()).isEqualTo(1);
        assertThat(testee.getLoginId()).isEqualTo("soramame");
        assertThat(testee.getPassword()).isEqualTo("password");
        assertThat(testee.getUserName()).isEqualTo("edamame");
        assertThat(testee.getPhoneNumber()).isEqualTo("12345");
        assertThat(testee.getUserType()).isEqualTo(UserType.MEMBER);
        assertThat(testee.isAdmin()).isFalse();
    }

    @Test
    void testPropetyValidation() {

        // エラーがないこと
        UserAccount u = createAllOKUserAccount();
        Set<ConstraintViolation<UserAccount>> result = validator.validate(u);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // IDエラー
        // -- グループ指定なし→未実行なのでエラーなし
        u.setId(0);
        result = validator.validate(u);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();
        // -- グループ指定あり→バリデート実行でエラー
        result = validator.validate(u, Update.class);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("id")
            .hasMessageEndingWith("Min.message");

        // ログインIDエラー(null)
        u = createAllOKUserAccount();
        u.setLoginId(null);
        result = validator.validate(u);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("loginId")
            .hasMessageEndingWith("NotNull.message");

        // ログインIDエラー(空文字列)
        u = createAllOKUserAccount();
        u.setLoginId("");
        result = validator.validate(u);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("loginId")
            .hasMessageEndingWith("Size.message");


        // ログインIDエラー(5文字未満)
        u = createAllOKUserAccount();
        u.setLoginId("1234");
        result = validator.validate(u);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("loginId")
            .hasMessageEndingWith("Size.message");

        // パスワードエラー(null)
        u = createAllOKUserAccount();
        u.setPassword(null);
        result = validator.validate(u);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("password")
            .hasMessageEndingWith("NotNull.message");

        // パスワードエラー(空文字列)
        u = createAllOKUserAccount();
        u.setPassword("");
        result = validator.validate(u);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("password")
            .hasMessageEndingWith("Size.message");

        // パスワードエラー(5文字未満)
        u = createAllOKUserAccount();
        u.setPassword("1234");
        result = validator.validate(u);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("password")
            .hasMessageEndingWith("Size.message");

        // ユーザ名(null)
        u = createAllOKUserAccount();
        u.setUserName(null);
        result = validator.validate(u);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("userName")
            .hasMessageEndingWith("NotBlank.message");

        // 電話番号(使用可能文字以外)
        u = createAllOKUserAccount();
        u.setPhoneNumber("12345%");
        result = validator.validate(u);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("phoneNumber")
            .hasMessageEndingWith("PhoneNumberCharacter.message");

        // 電話番号(14文字より大きい)
        u = createAllOKUserAccount();
        u.setPhoneNumber("123456789012345");
        result = validator.validate(u);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("phoneNumber")
            .hasMessageEndingWith("Size.message");

        // 連絡先(40文字より大きい)
        u = createAllOKUserAccount();
        u.setContact("12345678901234567890123456789012345678901");
        result = validator.validate(u);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("contact")
            .hasMessageEndingWith("Size.message");

        // ユーザ区分(null)
        u = createAllOKUserAccount();
        u.setUserType(null);
        result = validator.validate(u);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("userType")
            .hasMessageEndingWith("NotNull.message");
    }

    private UserAccount createAllOKUserAccount() {
        UserAccount u = new UserAccount();
        u.setId(1);
        u.setLoginId("LoginName");
        u.setPassword("Password");
        u.setUserName("UserName");
        u.setPhoneNumber("03-1234-5689");
        u.setAdmin(true);
        return u;
    }

}
