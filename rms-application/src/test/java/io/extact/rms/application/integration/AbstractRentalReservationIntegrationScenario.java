package io.extact.rms.application.integration;

import static io.extact.rms.test.assertj.ToStringAssert.*;
import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import io.extact.rms.application.RentalReservationApplication;
import io.extact.rms.application.common.LoginUserUtils;
import io.extact.rms.application.common.ServiceLoginUser;
import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.application.domain.Reservation;
import io.extact.rms.application.domain.UserAccount;
import io.extact.rms.application.domain.UserAccount.UserType;
import io.extact.rms.application.exception.BusinessFlowException;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
abstract class AbstractRentalReservationIntegrationScenario {

    @Inject
    protected RentalReservationApplication application;
    private ScenarioContext context = new ScenarioContext();

    static class ScenarioContext {
        private UserAccount loginUser;
        private RentalItem addedRentalItem;
    }

    @Test
    @Order(1)
    void testAddUserAccount() {
        // -----------------------------------
        // ユーザの登録
        // -----------------------------------
        UserAccount newUser = new UserAccount();
        newUser.setLoginId("testUser");
        newUser.setPassword("userTest");
        newUser.setUserName("テスト太郎");
        newUser.setContact("なぞなぞ");
        newUser.setPhoneNumber("1234");
        newUser.setUserType(UserType.ADMIN);

        UserAccount addedUser = application.addUserAccount(newUser);

        // 登録を依頼した内容と登録された内容が同じか？
        // 登録したユーザに発番されたIDが既存のレコード数(3) +1であること
        // その他のユーザ情報が登録したユーザの情報と同じか
        newUser.setId(3 + 1);
        assertThatToString(addedUser).isEqualTo(newUser);

        // -----------------------------------
        // 登録したユーザでログインできるか
        // -----------------------------------
        UserAccount loginUser = application.authenticate(addedUser.getLoginId(), addedUser.getPassword());

        // 取得したログインユーザの情報が登録したユーザの情報と同じであること
        assertThat(loginUser).isNotNull();
        assertThatToString(addedUser).isEqualTo(loginUser);

        // -----------------------------------
        // 登録したユーザを再登録（重複エラー）
        // -----------------------------------
        // ログインIDが重複
        assertThatThrownBy(() ->
            application.addUserAccount(addedUser)
        ).isInstanceOf(BusinessFlowException.class);

        // @Order(3)の事前条件として設定
        context.loginUser = loginUser;
    }

    @Test
    @Order(2)
    void testAddRentalItem() {
        // -----------------------------------
        // レンタル品の登録
        // -----------------------------------
        RentalItem newItem = new RentalItem();
        newItem.setSerialNo("testSerial");
        newItem.setItemName("1234");

        RentalItem addedItem = application.addRentalItem(newItem);

        // 登録を依頼した内容と登録された内容が同じか
        // 登録したレンタル品に発番されたIDが既存のレコード数(4) +1であること
        // その他のレンタル品の情報が登録したレンタル品の情報と同じか
        newItem.setId(4 + 1);
        assertThatToString(addedItem).isEqualTo(newItem);

        // -----------------------------------
        // 登録したレンタル品を再登録（重複エラー）
        // -----------------------------------
        // シリアル番号が重複
        assertThatThrownBy(() ->
            application.addRentalItem(addedItem)
        ).isInstanceOf(BusinessFlowException.class);

        // @Order(3)の事前条件として設定
        context.addedRentalItem = addedItem;
    }

    @Test
    @Order(3)
    void testAddReservation() {
        // -----------------------------------
        // @Order(1)で登録したログインユーザと@Order(2)で登録したレンタル品で予約をできるか
        // -----------------------------------

        // 事前条件の取得
        var loginUser = this.context.loginUser;
        var addedItem = this.context.addedRentalItem;


        // 登録したレンタル品がレンタル品一覧に含まれていること
        var selectedItem = application.getAllRentalItems().stream()
                .filter(item -> item.getId().equals(addedItem.getId()))
                .findFirst()
                .orElse(null);
        assertThat(selectedItem).isNotNull();

        // 登録したユーザとレンタル品で予約
        var startDateTime = LocalDateTime.now().plusDays(1).truncatedTo(MINUTES);
        var endDateTime = startDateTime.plusDays(1);
        var note = "備考のメモ";
        var newReservation = Reservation.ofTransient(startDateTime, endDateTime, note, addedItem.getId(), loginUser.getId());

        var addedReservation = application.addReservation(newReservation);

        // 予約の固有属性が登録を依頼した内容と同じであること
        assertThat(addedReservation.getStartDateTime()).isEqualTo(startDateTime);
        assertThat(addedReservation.getEndDateTime()).isEqualTo(endDateTime);
        assertThat(addedReservation.getNote()).isEqualTo(note);

        // 予約に発番されたIDが既存のレコード数+1であること
        assertThat(addedReservation.getId()).isEqualTo(3 + 1);
        // 予約のユーザIDがログインユーザのIDと同じであること
        assertThat(addedReservation.getUserAccountId()).isEqualTo(loginUser.getId());
        // 予約のレンタル品IDが登録したレンタル品IDと同じであること
        assertThat(addedReservation.getRentalItemId()).isEqualTo(addedItem.getId());

        // 予約についているユーザインスタンスとログインユーザの内容が同じであること
        assertThatToString(addedReservation.getUserAccount()).isEqualTo(loginUser);
        // 予約についているレンタル品インスタンスと登録したレンタル品の内容が同じであること
        assertThatToString(addedReservation.getRentalItem()).isEqualTo(addedItem);

        // -----------------------------------
        // 予約照会で登録した予約を参照できるか
        // -----------------------------------
        List<Reservation> reservations = application.findReservationByRentalItemAndStartDate(addedReservation.getRentalItemId(),
                addedReservation.getStartDateTime().toLocalDate());

        // 該当の予約が1件であること
        assertThat(reservations).hasSize(1);
        // 該当の予約が登録した予約と同じであること
        assertThatToString(reservations.get(0)).isEqualTo(addedReservation);
    }

    @Test
    @Order(4)
    void testCancelReservation() {
        // -----------------------------------
        // @Order(3)で登録した予約をキャンセルできるか
        // -----------------------------------

        // 事前条件の取得と設定
        var loginUser = this.context.loginUser;
        LoginUserUtils.set(ServiceLoginUser.of(loginUser.getId(), null));

        // ログインユーザの予約一覧を取得
        var ownReservations = application.findReservationByReserverId(loginUser.getId());
        // 該当の予約が1件であること
        assertThat(ownReservations).hasSize(1);

        // キャンセル対象の予約を取得
        var cancelTarget = ownReservations.get(0);
        // キャンセルの実行
        application.cancelReservation(cancelTarget.getId());

        // 再度ログインユーザの予約一覧を取得
        ownReservations = application.findReservationByReserverId(loginUser.getId());
        // 自分の予約一覧に出てなこないこと
        assertThat(ownReservations).isEmpty();

        // 事後処理
        LoginUserUtils.remove();;
    }

    @Test
    @Order(5)
    void testReAddReservation() {
        // -----------------------------------
        // @Order(4)の予約をキャンセル後に予約ができるか（IDの発番が正しいか）
        // -----------------------------------
        // 事前条件の取得
        var loginUser = this.context.loginUser;
        var addedItem = this.context.addedRentalItem;

        // 登録したユーザとレンタル品で予約
        var startDateTime = LocalDateTime.now().plusDays(1).truncatedTo(MINUTES);
        var endDateTime = startDateTime.plusDays(1);
        var note = "備考のメモ";
        var newReservation = Reservation.ofTransient(startDateTime, endDateTime, note, addedItem.getId(), loginUser.getId());

        var addedReservation = application.addReservation(newReservation);

        // 登録された予約のIDがmax(id)+1であること
        assertThat(addedReservation.getId()).isEqualTo(expectedReregistrationId());

        // ログインユーザの予約一覧を取得
        var ownReservations = application.findReservationByReserverId(loginUser.getId());
        // 該当の予約が1件であること
        assertThat(ownReservations).hasSize(1);
    }

    @Test
    @Order(6)
    void testUpdateUserAccount() {
        // -----------------------------------
        // ユーザ情報を更新できるか
        // -----------------------------------
        // 一覧を取得
        var users = application.getAllUserAccounts();
        assertThat(users).hasSize(4);

        // 一覧から更新対象を選択
        var updateTarget = users.get(0);

        // ユーザ名を変更
        updateTarget.setPassword("UPDATE");

        // 期待値の取得と編集
        var expected = application.get(UserAccount.class, updateTarget.getId());
        expected.setPassword("UPDATE");

        // 更新の実行
        var resultUser = application.updateUserAccount(updateTarget);

        assertThatToString(resultUser).isEqualTo(expected);
        assertThatToString(application.get(UserAccount.class, updateTarget.getId())).isEqualTo(expected); // 読み返して比較
    }

    protected abstract int expectedReregistrationId(); // 予約を再登録した際に発番される期待ID
}
