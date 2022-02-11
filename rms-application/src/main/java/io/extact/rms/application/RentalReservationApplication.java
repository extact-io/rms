package io.extact.rms.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.application.domain.Reservation;
import io.extact.rms.application.domain.UserAccount;
import io.extact.rms.application.exception.BusinessFlowException;

/**
 * レンタル予約アプリケーションインターフェース.
 */
public interface RentalReservationApplication {

    /**
     * ユーザをパスワードで認証する。
     *
     * @param loginId 認証するユーザのログインID
     * @param password 認証パスワード
     * @return 認証ユーザ。認証できなかった場合はnull
     * @throws BusinessFlowException ユーザIDまたはパスワードに一致するユーザがいない
     */
    UserAccount authenticate(String loginId, String password) throws BusinessFlowException;

    /**
     * 指定されたIDのエンティティを取得する。
     *
     * @param <T> エンティティクラス
     * @param entityClass 取得するエンティティクラス
     * @param id ID
     * @return エンティティ
     */
    <T> T get(Class<T> entityClass, int id);

    /**
     * 指定されたレンタル品と利用開始日に対する予約を取得する。
     *
     * @param rentalItemId 予約のレンタル品ID
     * @param startDate 予約の利用開始日
     * @return 予約リスト（該当なしは例外を送出）
     * @throws BusinessFlowException 該当なし
     */
    List<Reservation> findReservationByRentalItemAndStartDate(Integer rentalItemId, LocalDate startDate) throws BusinessFlowException;

    /**
     * 指定されたユーザが予約者の予約を取得する。
     *
     * @param reserverId 予約者のユーザID
     * @return 該当のリスト。該当なしは空リスト
     */
    List<Reservation> findReservationByReserverId(int reserverId);

    /**
     * 指定されたレンタル品に対する予約を取得する。
     *
     * @param rentalItemId レンタル品ID
     * @return 該当のリスト。該当なしは空リスト
     */
    List<Reservation> findReservationByRentalItemId(int rentalItemId);

    /**
     * 該当期間に予約可能なレンタル品を返す。
     *
     * @param from 利用開始日時
     * @param to 利用終了日時
     * @return 該当のリスト。該当なしは空リスト
     */
    List<RentalItem> findCanRentedItemAtTerm(LocalDateTime from, LocalDateTime to);

    /**
     * レンタル品が該当期間に予約可能かを返す
     *
     * @param rentalItemId レンタル品ID
     * @param from 利用開始日時
     * @param to 利用終了日時
     * @return 可能な場合はtrue
     */
    boolean canRentedItemAtTerm(int rentalItemId, LocalDateTime from, LocalDateTime to);

    /**
     * 予約の全件取得。
     * <br>
     * @return 予約の全件。該当なしは空リスト
     */
    List<Reservation> getAllReservations();

    /**
     * レンタル品の全件取得。
     *
     * @return レンタル品の全件。該当なしは空リスト
     */
    List<RentalItem> getAllRentalItems();

    /**
     * ユーザの全件取得。
     * <br>
     * @return ユーザの全件。該当なしは空リスト
     */
    List<UserAccount> getAllUserAccounts();

    /**
     * レンタル品を予約する。
     * <p>
     * @param addReservation 登録する予約（idはnull）
     * @return 登録された予約（idが設定されている）
     * @throws BusinessFlowException 該当するレンタル品が存在しない場合、または期間が重複する予約が既に登録されている場合
     */
    Reservation addReservation(Reservation addReservation) throws BusinessFlowException;

    /**
     * レンタル品を登録する。
     * <p>
     * @param addRentalItem 登録レンタル品（idはnull）
     * @return 登録されたレンタル品（idが設定されている）
     * @throws BusinessFlowException 同一シリアル番号のレンタル品が既に登録されている場合
     */
    RentalItem addRentalItem(RentalItem addRentalItem) throws BusinessFlowException;

    /**
     * ユーザアカウントを登録する。
     * <p>
     * @param addUserAccount 登録ユーザ（idはnull）
     * @return 登録されたユーザアカウント（idが設定されている）
     * @throws BusinessFlowException 同一ログインIDのユーザが既に登録されている場合
     */
    UserAccount addUserAccount(UserAccount addUserAccount) throws BusinessFlowException;

    /**
     * 予約を更新する。
     * <br>
     * @param updateReservation 更新する予約
     * @return 更新された予約。更新対象がない場合はnull
     */
    Reservation updateReservation(Reservation updateReservation);

    /**
     * レンタル品を更新する。
     * <br>
     * @param updateRentalItem 更新するレンタル品
     * @return 更新されたレンタル品。更新対象がない場合はnull
     */
    RentalItem updateRentalItem(RentalItem updateRentalItem);

    /**
     * ユーザアカウントを更新する。
     * <p>
     * @param updateUserAccount 更新ユーザ
     * @return 更新されたユーザアカウント。更新対象がない場合はnull
     */
    UserAccount updateUserAccount(UserAccount updateUserAccount);

    /**
     * 予約を削除する。
     * <br>
     * @param reservationId 予約ID
     * @throws BusinessFlowException 該当の予約が存在しない場合
     */
    void deleteReservation(int reservationId) throws BusinessFlowException;

    /**
     * レンタル品を削除する。
     * 対象のレンタル品を参照する予約が存在する場合は削除は行わずエラーにする。
     * <br>
     * @param rentalItemId レンタル品ID
     * @throws BusinessFlowException 該当のレンタル品が存在しない場合。対象のレンタル品を参照する予約が存在する場合
     */
    void deleteRentalItem(int rentalItemId) throws BusinessFlowException;

    /**
     * ユーザを削除する。
     * 対象のユーザを参照する予約が存在する場合は削除は行わずエラーにする。
     * <br>
     * @param userAccountId ユーザアカウントID
     * @throws BusinessFlowException 該当のユーザアカウントが存在しない場合。対象のユーザを参照する予約が存在する場合
     */
    void deleteUserAccount(int userAccountId) throws BusinessFlowException;

    /**
     * 予約をキャンセルする。
     * <br>
     * @param reservationId 予約ID
     * @throws BusinessFlowException 該当の予約が存在しない場合。予約者以外が取消を行っている場合
     */
    void cancelReservation(int reservationId) throws BusinessFlowException;

    /**
     * 自分のプロファイル情報を取得する。
     * <br>
     * @throws BusinessFlowException 該当のユーザアカウントが存在しない場合
     */
    UserAccount getOwnUserProfile() throws BusinessFlowException;

    /**
     * 自分のプロファイル情報を更新する。
     * 自分以外の情報を更新しようとした場合は禁止操作として403を返す。
     * <br>
     * @param updateUserAccount 更新情報
     * @return 更新されたユーザアカウント。更新対象がない場合はnull
     */
    UserAccount updateUserProfile(UserAccount updateUserAccount);

}
