package io.extact.rms.application.domain;

import static io.extact.rms.application.domain.ReservationTest.SetPattern.*;
import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.jupiter.api.Test;

import io.extact.rms.application.domain.Reservation.DateTimePeriod;
import io.extact.rms.application.domain.constraint.ValidationGroups.Add;
import io.extact.rms.application.domain.constraint.ValidationGroups.Update;
import io.extact.rms.test.assertj.ConstraintViolationSetAssert;

class ReservationTest extends PropertyTest {

    @Override
    protected Class<?> getTargetClass() {
        return Reservation.class;
    }

    @Test
    void testSetId() throws Exception {
        Reservation testee = new Reservation();
        testee.setId(100);
        Field id = this.getField("id");

        assertThat(id).isNotNull();
        assertThat(id.get(testee)).isEqualTo(100);
    }

    @Test
    void testGetId() throws Exception {
        Reservation testee = new Reservation();
        Field id = this.getField("id");

        assertThat(id).isNotNull();

        id.set(testee, 100);
        assertThat(testee.getId()).isEqualTo(100);
    }

    @Test
    void testSetStartDateTime() throws Exception {
        Reservation testee = new Reservation();
        LocalDateTime now = LocalDateTime.now().truncatedTo(MINUTES);
        testee.setStartDateTime(now);
        Field startDate = this.getField("startDateTime");

        assertThat(startDate).isNotNull();
        assertThat((LocalDateTime) startDate.get(testee)).isEqualTo(now);
    }

    @Test
    void testGetStartDateTime() throws Exception {
        Reservation testee = new Reservation();
        LocalDateTime now = LocalDateTime.now();
        Field startDateTime = this.getField("startDateTime");

        assertThat(startDateTime).isNotNull();

        startDateTime.set(testee, now);
        assertThat(testee.getStartDateTime()).isEqualTo(now);
    }

    @Test
    void testSetEndDateTime() throws Exception {
        Reservation testee = new Reservation();
        LocalDateTime now = LocalDateTime.now().truncatedTo(MINUTES);
        testee.setEndDateTime(now);
        Field endDate = this.getField("endDateTime");

        assertThat(endDate).isNotNull();
        assertThat((LocalDateTime) endDate.get(testee)).isEqualTo(now);
    }

    @Test
    void testGetEndDateTime() throws Exception {
        Reservation testee = new Reservation();
        LocalDateTime now = LocalDateTime.now();
        Field endDateTime = this.getField("endDateTime");

        assertThat(endDateTime).isNotNull();

        endDateTime.set(testee, now);
        assertThat(testee.getEndDateTime()).isEqualTo(now);
    }

    @Test
    void testSetNote() throws Exception {
        Reservation testee = new Reservation();
        testee.setNote("だって使うんだもん");
        Field note = this.getField("note");

        assertThat(note).isNotNull();
        assertThat(note.get(testee)).isEqualTo("だって使うんだもん");
    }

    @Test
    void testGetNote() throws Exception {
        Reservation testee = new Reservation();
        Field note = this.getField("note");

        assertThat(note).isNotNull();

        note.set(testee, "だって使うんだもん");
        assertThat(testee.getNote()).isEqualTo("だって使うんだもん");
    }

    @Test
    void testSetRentalItemId() throws Exception {
        Reservation testee = new Reservation();
        testee.setRentalItemId(101);
        Field rentalItemId = this.getField("rentalItemId");

        assertThat(rentalItemId).isNotNull();
        assertThat(rentalItemId.get(testee)).isEqualTo(101);
    }

    @Test
    void testGetRentalItemId() throws Exception {
        Reservation testee = new Reservation();
        Field rentalItemId = this.getField("rentalItemId");

        assertThat(rentalItemId).isNotNull();

        rentalItemId.set(testee, 101);
        assertThat(testee.getRentalItemId()).isEqualTo(101);
    }

    @Test
    void testSetUserAccountId() throws Exception {
        Reservation testee = new Reservation();
        testee.setUserAccountId(102);
        Field userAccountId = this.getField("userAccountId");

        assertThat(userAccountId).isNotNull();
        assertThat(userAccountId.get(testee)).isEqualTo(102);
    }

    @Test
    void testGetUserAccountId() throws Exception {
        Reservation testee = new Reservation();
        Field userAccountId = this.getField("userAccountId");

        assertThat(userAccountId).isNotNull();

        userAccountId.set(testee, 102);
        assertThat(testee.getUserAccountId()).isEqualTo(102);
    }

    @Test
    void testSetRentalItem() throws Exception {
        Reservation testee = new Reservation();
        RentalItem rentalItem = new RentalItem();
        testee.setRentalItem(rentalItem);
        Field field = this.getField("rentalItem");

        assertThat(field).isNotNull();
        assertThat(field.get(testee)).isEqualTo(rentalItem);
    }

    @Test
    void testGetRentalItem() throws Exception {
        Reservation testee = new Reservation();
        Field field = this.getField("rentalItem");

        assertThat(field).isNotNull();

        RentalItem rentalItem = new RentalItem();
        field.set(testee, rentalItem);
        assertThat(testee.getRentalItem()).isEqualTo(rentalItem);
    }

    @Test
    void testSetUserAccount() throws Exception {
        Reservation testee = new Reservation();
        UserAccount userAccount = new UserAccount();
        testee.setUserAccount(userAccount);
        Field field = this.getField("userAccount");

        assertThat(field).isNotNull();
        assertThat(field.get(testee)).isEqualTo(userAccount);
    }

    @Test
    void testGetUserAccount() throws Exception {
        Reservation testee = new Reservation();
        Field field = this.getField("userAccount");

        assertThat(field).isNotNull();

        UserAccount userAccount = new UserAccount();
        field.set(testee, userAccount);
        assertThat(testee.getUserAccount()).isEqualTo(userAccount);
    }

    @Test
    void testDateTimePeriod() {
        Reservation reserved = new Reservation();
        reserved.setStartDateTime(LocalDateTime.of(2020, 1, 1, 0, 1));
        reserved.setEndDateTime(LocalDateTime.of(2020, 12, 31, 23, 59));

        DateTimePeriod reservedPeriod = reserved.getReservePeriod();
        assertThat(reservedPeriod.getStartDateTime()).isEqualTo(LocalDateTime.of(2020, 1, 1, 0, 1));
        assertThat(reservedPeriod.getEndDateTime()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59));
    }

    @Test
    void testReservePeriod() {

        // reservedPeriod(2020/1/1 00:01-2020/12/31 23:59)
        Reservation reserved = new Reservation();
        reserved.setStartDateTime(LocalDateTime.of(2020, 1, 1, 0, 1));
        reserved.setEndDateTime(LocalDateTime.of(2020, 12, 31, 23, 59));

        DateTimePeriod reservedPeriod = reserved.getReservePeriod();

        // pattern1:終了日時(-2020/1/1 00:00) ＜ 予約済み利用開始日時
        Reservation request = new Reservation();
        request.setStartDateTime(LocalDateTime.of(2019, 12, 1, 0, 0));
        request.setEndDateTime(LocalDateTime.of(2020, 1, 1, 0, 0));
        DateTimePeriod requestPeriod = request.getReservePeriod();
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isFalse();

        // pattern2:終了日時(-2020/1/1 00:01) ＝ 予約済み利用開始日時
        request.setStartDateTime(LocalDateTime.of(2019, 12, 1, 0, 0));
        request.setEndDateTime(LocalDateTime.of(2020, 1, 1, 0, 1));
        requestPeriod = request.getReservePeriod();
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isTrue();

        // pattern3:予約済み利用開始日時      ＜ 終了日時-2020/1/1 00:02)
        request.setStartDateTime(LocalDateTime.of(2019, 12, 1, 0, 0));
        request.setEndDateTime(LocalDateTime.of(2020, 1, 1, 0, 2));
        requestPeriod = request.getReservePeriod();
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isTrue();

        // pattern4:予約済み期間に期間(2020/1/1 00:02 - 2020/12/31 23:58)が含まれる
        request.setStartDateTime(LocalDateTime.of(2020, 1, 1, 0, 2));
        request.setEndDateTime(LocalDateTime.of(2020, 12, 31, 23, 58));
        requestPeriod = request.getReservePeriod();
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isTrue();

        // pattern5:予約済み期間を期間(2020/1/1 00:00 - 2021/1/1 00:00)が含む
        request.setStartDateTime(LocalDateTime.of(2020, 1, 1, 0, 0));
        request.setEndDateTime(LocalDateTime.of(2021, 1, 1, 0, 0));
        requestPeriod = request.getReservePeriod();
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isTrue();

        // pattern6:予約済み期間と期間(2020/1/1 00:01 - 2020/12/31 23:59)同じ
        request.setStartDateTime(LocalDateTime.of(2020, 1, 1, 0, 1));
        request.setEndDateTime(LocalDateTime.of(2020, 12, 31, 23, 59));
        requestPeriod = request.getReservePeriod();
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isTrue();

        // pattern7:予約済み利用終了日時 ＝ 開始日時(2020/12/31 23:59-)
        request.setStartDateTime(LocalDateTime.of(2020, 12, 31, 23, 59));
        request.setEndDateTime(LocalDateTime.of(2021, 1, 1, 0, 0));
        requestPeriod = request.getReservePeriod();
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isTrue();

        // pattern8:予約済み利用終了日時 ＜ 開始日時(2021/1/1 00:00-)
        request.setStartDateTime(LocalDateTime.of(2021, 1, 1, 0, 0));
        request.setEndDateTime(LocalDateTime.of(2021, 1, 1, 0, 1));
        requestPeriod = request.getReservePeriod();
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isFalse();
    }

    @Test
    void testNewInstance() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();

        Reservation testee = Reservation.of(1, startDate, endDate, "note", 2, 3);

        assertThat(testee.getId()).isEqualTo(1);
        assertThat(testee.getStartDateTime()).isEqualTo(startDate);
        assertThat(testee.getEndDateTime()).isEqualTo(endDate);
        assertThat(testee.getNote()).isEqualTo("note");
        assertThat(testee.getRentalItemId()).isEqualTo(2);
        assertThat(testee.getUserAccountId()).isEqualTo(3);
    }


    // ----------------------------------------------------- constraints tests

    @Test
    void testBefoeAterValidate() {
        Reservation r = createAllOKReservation(ALL);
        r.setStartDateTime(LocalDateTime.now());
        r.setEndDateTime(r.getStartDateTime().minusYears(1));

        Set<ConstraintViolation<Reservation>> result = validator.validate(r);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasMessageEndingWith("BeforeAfterDateTime.message");
    }

    @Test
    void testPropetyValidation() {

        // エラーがないこと
        Reservation r = createAllOKReservation(ALL);
        Set<ConstraintViolation<Reservation>> result = validator.validate(r);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // IDエラー
        // -- グループ指定なし→未実行なのでエラーなし
        r.setId(0);
        result = validator.validate(r);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();
        // -- グループ指定あり→バリデート実行でエラー
        result = validator.validate(r, Update.class);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("id")
            .hasMessageEndingWith("Min.message");

        // 利用開始日エラー(null)
        r = createAllOKReservation(NONE_START_DATETIME);
        // -- グループ指定なし→未実行なのでエラーなし
        result = validator.validate(r);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("startDateTime")
            .hasMessageEndingWith("NotNull.message");
        // -- グループ指定あり→バリデート実行でエラー
        result = validator.validate(r, Add.class);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("startDateTime")
            .hasMessageEndingWith("NotNull.message");

        // 利用開始日エラー(過去日)
        r.setStartDateTime(LocalDateTime.now().minusDays(1));
        result = validator.validate(r, Add.class);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("startDateTime")
            .hasMessageEndingWith("Future.message");

        // 利用終了日エラー(null)
        r = createAllOKReservation(NONE_END_DATETIME);
        result = validator.validate(r);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("endDateTime")
            .hasMessageEndingWith("NotNull.message");

        // メモ(64文字以内)
        r = createAllOKReservation(ALL);
        r.setNote("１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４５６７８９０１２３４"); // 境界値:OK
        result = validator.validate(r);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // メモ(64文字より大きい)
        r = createAllOKReservation(ALL);
        r.setNote("12345678901234567890123456789012345678901234567890123456789012345"); // 境界値:NG
        result = validator.validate(r);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("note")
            .hasMessageEndingWith("Size.message");

        // レンタル品IDエラー
        r = createAllOKReservation(ALL);
        r.setRentalItemId(0);
        result = validator.validate(r);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("rentalItemId")
            .hasMessageEndingWith("Min.message");

        // ユーザIDエラー
        r = createAllOKReservation(ALL);
        r.setUserAccountId(0);
        result = validator.validate(r);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("userAccountId")
            .hasMessageEndingWith("Min.message");

    }

    private Reservation createAllOKReservation(SetPattern setPattern) {
        Reservation r = new Reservation();
        r.setId(1);
        if (setPattern != NONE_START_DATETIME) {
            r.setStartDateTime(LocalDateTime.now().plusMinutes(1));
        }
        if (setPattern != NONE_END_DATETIME) {
            r.setEndDateTime(LocalDateTime.now().plusDays(1));
        }
        r.setNote("note");
        r.setRentalItemId(1);
        r.setUserAccountId(1);
        return r;
    }

    enum SetPattern {
        ALL,
        NONE_START_DATETIME,
        NONE_END_DATETIME
    }
}
