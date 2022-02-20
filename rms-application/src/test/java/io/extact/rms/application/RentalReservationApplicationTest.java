package io.extact.rms.application;

import static io.extact.rms.application.TestUtils.*;
import static io.extact.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.extact.rms.application.TestUtils.PathResolverParameterExtension;
import io.extact.rms.application.common.LoginUserUtils;
import io.extact.rms.application.common.ServiceLoginUser;
import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.application.domain.Reservation;
import io.extact.rms.application.domain.UserAccount;
import io.extact.rms.application.domain.UserAccount.UserType;
import io.extact.rms.application.exception.BusinessFlowException;
import io.extact.rms.application.exception.BusinessFlowException.CauseType;
import io.extact.rms.application.persistence.file.io.PathResolver;

@ExtendWith(PathResolverParameterExtension.class)
class RentalReservationApplicationTest {

    private RentalReservationApplication target;

    @BeforeEach
    void setup(PathResolver pathResolver) throws Exception {
        target = newRentalReservationApplication(pathResolver);
    }

    @Test
    void testAuthenticate() {
        var expect = UserAccount.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER);
        var actual = target.authenticate("member1", "member1");
        assertThatToString(actual).isEqualTo(expect);
    }

    @ParameterizedTest
    @CsvSource({ "soramame, hoge", "hoge, soramame", "hoge, hoge" })
    void testCannotAuthenticate(String id, String password) {
        var thrown = catchThrowableOfType(() ->
            target.authenticate(id, password),
            BusinessFlowException.class
        );
        assertThat(thrown).isNotNull();
    }


    @Test
    void testGetAllReservations() {
        var expect = List.of(
                Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1),
                Reservation.of(2, LocalDateTime.of(2020, 4, 1, 16, 0, 0), LocalDateTime.of(2020, 4, 1, 18, 0, 0), "メモ2", 3, 2),
                Reservation.of(3, LocalDateTime.of(2021, 4, 1, 10, 0, 0), LocalDateTime.of(2021, 4, 1, 12, 0, 0), "メモ3", 3, 1)
            );
        var actual = target.getAllReservations();
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    void testGetAllRentalItems() {
        var expect = List.of(
                RentalItem.of(1, "A0001", "レンタル品1号"),
                RentalItem.of(2, "A0002", "レンタル品2号"),
                RentalItem.of(3, "A0003", "レンタル品3号"),
                RentalItem.of(4, "A0004", "レンタル品4号")
            );
        var actual = target.getAllRentalItems();
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    void testGetAllUserAccounts() {
        var expected = List.of(
                UserAccount.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER),
                UserAccount.of(2, "member2", "member2", "メンバー2", "080-1111-2222", "連絡先2", UserType.MEMBER),
                UserAccount.of(3, "admin", "admin", "管理者", "050-1111-2222", "連絡先3", UserType.ADMIN)
                );
        var actual = target.getAllUserAccounts();
        assertThatToString(actual).containsExactlyElementsOf(expected);
    }

    @Test
    void testFindReservationByRentalItemAndStartDate() {
        var expect = List.of(
                Reservation.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1),
                Reservation.of(2, LocalDateTime.of(2020, 4, 1, 16, 0, 0), LocalDateTime.of(2020, 4, 1, 18, 0, 0), "メモ2", 3, 2)
            );
        var actual = target.findReservationByRentalItemAndStartDate(3, LocalDate.of(2020, 4, 1));
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @ParameterizedTest
    @CsvSource({ "903, 2004/04/01", "1, 2004/07/10", "903, 2004/07/10" })
    void testCannotFindReservationByRentalItemAndStartDate(int rentalItemId, String date) {
        var pttn = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        var thrown = catchThrowableOfType(() ->
            target.findReservationByRentalItemAndStartDate(rentalItemId, LocalDate.parse(date, pttn)),
            BusinessFlowException.class
        );
        assertThat(thrown).isNotNull();
    }

    @Test
    void testAddReservation() {
        var addReservation = Reservation.ofTransient(LocalDateTime.of(2021, 4, 18, 10, 0, 0), LocalDateTime.of(2021, 5, 16, 20, 0, 0), "メモ4", 3, 1);
        var expect = Reservation.of(4, LocalDateTime.of(2021, 4, 18, 10, 0, 0), LocalDateTime.of(2021, 5, 16, 20, 0, 0), "メモ4", 3, 1);
        var actual = target.addReservation(addReservation);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFailToAddReservationForBadItem() {
        // rentalItemId=999はマスタ登録なし
        var addReservation = Reservation.of(null, LocalDateTime.of(2021, 4, 18, 10, 0, 0), LocalDateTime.of(2021, 5, 16, 20, 0, 0), "メモ4", 999, 1);
        var thrown = catchThrowableOfType(() ->
            target.addReservation(addReservation),
            BusinessFlowException.class
        );
        assertThat(thrown).isNotNull();
    }

    @Test
    void testFailToAddReservationForDuplicate() {
        // 2020/4/1 16:00-18:00 で既に予約あり
        var addReservation = Reservation.of(null, LocalDateTime.of(2020, 4, 1, 17, 0, 0), LocalDateTime.of(2020, 4, 1, 19, 0, 0), "メモ4", 3, 1);
        var thrown = catchThrowableOfType(() ->
            target.addReservation(addReservation),
            BusinessFlowException.class
        );
        assertThat(thrown).isNotNull();
    }

    @Test
    void testFindReservationByReserverId() {
        // 1件ヒットパターン
        var actual = target.findReservationByReserverId(2);
        assertThat(actual).hasSize(1);
        // 2件ヒットパターン
        actual = target.findReservationByReserverId(1);
        assertThat(actual).hasSize(2);
        // 0件ヒットパターン
        actual = target.findReservationByReserverId(3);
        assertThat(actual).isEmpty();
    }

    @Test
    void testFindReservationByRentalItemId() {
        // 3件ヒットパターン
        var actual = target.findReservationByRentalItemId(3);
        assertThat(actual).hasSize(3);
        // 0件ヒットパターン
        actual = target.findReservationByRentalItemId(1);
        assertThat(actual).isEmpty();
    }

    @Test
    void testFindCanRentedItemAtTerm() {
        // 4/1 9:00-11:00で予約可能なレンタル品 => 1, 2, 4
        var actual = target.findCanRentedItemAtTerm(LocalDateTime.of(2020, 4, 1, 9, 0), LocalDateTime.of(2020, 4, 1, 11, 0));
        var actualIds = actual.stream().map(RentalItem::getId).collect(Collectors.toList());
        assertThat(actualIds).containsOnly(1, 2, 4);

        // 4/2 9:00-11:00で予約可能なレンタル品 => 1, 2, 3, 4
        actual = target.findCanRentedItemAtTerm(LocalDateTime.of(2020, 4, 2, 9, 0), LocalDateTime.of(2020, 4, 2, 11, 0));
        actualIds = actual.stream().map(RentalItem::getId).collect(Collectors.toList());
        assertThat(actualIds).containsOnly(1, 2, 3, 4);
    }

    @Test
    void testCanRentedItemAtTerm() {
        // 4/1 13:00-15:00でIDが3のレンタル品がレンタル可能か？
        var actual = target.canRentedItemAtTerm(3, LocalDateTime.of(2020, 4, 1, 13, 0), LocalDateTime.of(2020, 4, 1, 15, 0));
        assertThat(actual).isTrue();

        // 4/1 9:00-11:00でIDが3のレンタル品がレンタル可能か？
        actual = target.canRentedItemAtTerm(3, LocalDateTime.of(2020, 4, 1, 9, 0), LocalDateTime.of(2020, 4, 1, 11, 0));
        assertThat(actual).isFalse();
    }

    @Test
    void testAddRentalItem() {
        var addRentalItem = RentalItem.ofTransient("A0005", "レンタル品5号");
        var expect = RentalItem.of(5, "A0005", "レンタル品5号");
        var actual = target.addRentalItem(addRentalItem);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFailToAddRentalItem() {
        // "A0004"は既に登録済みのSerialNo
        var addRentalItem = RentalItem.ofTransient("A0004", "レンタル品5号");
        var thrown = catchThrowableOfType(() ->
            target.addRentalItem(addRentalItem),
            BusinessFlowException.class
        );
        assertThat(thrown).isNotNull();
    }

    @Test
    void testAddUserAccount() {
        var addUserAccount = UserAccount.ofTransient("member3", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER);
        var expect = UserAccount.of(4, "member3", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER);
        UserAccount actual = target.addUserAccount(addUserAccount);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFailToAddUserAccount() {
        // "member1"のloginIdは既に登録済み
        var addUserAccount = UserAccount.ofTransient("member1", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER);
        var thrown = catchThrowableOfType(() ->
            target.addUserAccount(addUserAccount),
            BusinessFlowException.class
        );
        assertThat(thrown).isNotNull();
    }

    @Test
    void testCancelReservation() {
        LoginUserUtils.set(ServiceLoginUser.of(2, null)); // 事前条件
        target.cancelReservation(2);
    }

    @Test
    void testReservationUpdate() {
        var update = target.get(Reservation.class, 1);
        update.setNote("UPDATE");
        var result = target.updateReservation(update);

        assertThat(result.getNote()).isEqualTo("UPDATE");
        assertThatToString(update).isEqualTo(target.get(Reservation.class, 1));
    }

    @Test
    void testFailToUpdateReservation() {
        var update = Reservation.of(999, LocalDateTime.now(), LocalDateTime.now().plusHours(1), "memo", 1, 1);
        var thrown = catchThrowableOfType(() ->
            target.updateReservation(update),
            BusinessFlowException.class
        );
        assertThat(thrown).isNotNull();
    }

    @Test
    void testRentalItemUpdate() {
        var update = target.get(RentalItem.class, 1);
        update.setItemName("UPDATE");
        var result = target.updateRentalItem(update);

        assertThat(result.getItemName()).isEqualTo("UPDATE");
        assertThatToString(update).isEqualTo(target.get(RentalItem.class, 1));
    }

    @Test
    void testFailToUpdateRentalItem() {
        var update = RentalItem.of(999, null, null);
        var thrown = catchThrowableOfType(() ->
            target.updateRentalItem(update),
            BusinessFlowException.class
        );
        assertThat(thrown).isNotNull();
    }

    @Test
    void testUserAccopuntUpdate() {
        var updateUser = target.get(UserAccount.class, 1);
        updateUser.setUserName("UPDATE");
        var resultUser = target.updateUserAccount(updateUser);

        assertThat(resultUser.getUserName()).isEqualTo("UPDATE");
        assertThatToString(resultUser).isEqualTo(target.get(UserAccount.class, 1));
    }

    @Test
    void testFailToUpdateUserAccount() {
        var updateUser = UserAccount.of(999, "member1", "member3", "メンバー3", "030-1111-2222", "連絡先4", UserType.MEMBER);
        var thrown = catchThrowableOfType(() ->
            target.updateUserAccount(updateUser),
            BusinessFlowException.class
        );
        assertThat(thrown).isNotNull();
    }

    @Test
    void testReservationDelete() {
        target.deleteReservation(1);
        assertThat(target.get(Reservation.class, 1)).isNull();
    }

    @Test
    void testFailToDeleteReservationNotFound() {
        var thrown = catchThrowable(() -> target.deleteReservation(999));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testRentalItemDelete() {
        target.deleteRentalItem(1);
        assertThat(target.get(RentalItem.class, 1)).isNull();
    }

    @Test
    void testFailToDeleteRentalItemNotFound() {
        var thrown = catchThrowable(() -> target.deleteRentalItem(999));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testFailToDeleteRentalItemRefered() {
        var thrown = catchThrowable(() -> target.deleteRentalItem(3));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.REFERED);
    }

    @Test
    void testUserAccountDelete() {
        target.deleteUserAccount(3);
        assertThat(target.get(UserAccount.class, 3)).isNull();
    }

    @Test
    void testFailToDeleteUserAccountNotFound() {
        var thrown = catchThrowable(() -> target.deleteUserAccount(999));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.NOT_FOUND);
    }

    @Test
    void testFailToDeleteUserAccountRefered() {
        var thrown = catchThrowable(() -> target.deleteUserAccount(1));
        assertThat(thrown).isInstanceOf(BusinessFlowException.class);
        assertThat(((BusinessFlowException) thrown).getCauseType()).isEqualTo(CauseType.REFERED);
    }

    @Test
    void testGetOwnProfile() {
        LoginUserUtils.set(ServiceLoginUser.of(1, null)); // 事前条件
        var ownProfile = target.getOwnUserProfile();
        assertThat(ownProfile).isNotNull();
        assertThat(ownProfile.getId()).isEqualTo(1);
    }

    @Test
    void testFailToGetOwnProfile() {
        LoginUserUtils.set(ServiceLoginUser.of(99, null)); // 事前条件
        var thrown = catchThrowableOfType(() ->
            target.getOwnUserProfile(),
            BusinessFlowException.class
        );
        assertThat(thrown).isNotNull();
    }

    @Test
    void testUpdateOwnProfile() {
        LoginUserUtils.set(ServiceLoginUser.of(1, null)); // 事前条件
        var updateUser = target.get(UserAccount.class, 1);
        updateUser.setUserName("UPDATE");
        var resultUser = target.updateUserProfile(updateUser);

        assertThat(resultUser.getUserName()).isEqualTo("UPDATE");
        assertThatToString(resultUser).isEqualTo(target.get(UserAccount.class, 1));
    }

    @Test
    void testFailToUpdateOwnProfile() {
        LoginUserUtils.set(ServiceLoginUser.of(2, null)); // 事前条件
        var updateUser = target.get(UserAccount.class, 1);
        var thrown = catchThrowableOfType(() ->
            target.updateUserProfile(updateUser),
            BusinessFlowException.class
        );
        assertThat(thrown).isNotNull();
    }
}
