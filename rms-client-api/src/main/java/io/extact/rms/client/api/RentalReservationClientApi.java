package io.extact.rms.client.api;

import java.time.LocalDate;
import java.util.List;

import io.extact.rms.client.api.dto.RentalItemClientDto;
import io.extact.rms.client.api.dto.ReservationClientDto;
import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.client.api.exception.BusinessFlowClientException;

/**
 * レンタル予約アプリケーションのClient向けインターフェース
 */
public interface RentalReservationClientApi {

    /**
     * ユーザをパスワードで認証する。
     * <p>
     * @param loginId 認証するユーザのログインID
     * @param password 認証パスワード
     * @return 認証ユーザ。認証できなかった場合はnull
     * @throws BusinessFlowClientException ユーザIDまたはパスワードに一致するユーザがいない
     */
    UserAccountClientDto authenticate(String loginId, String password) throws BusinessFlowClientException;

    /**
     * 指定されたレンタル品と利用開始日に対する予約を取得する。
     * <p>
     * @param rentalItemId 予約のレンタル品ID
     * @param startDate 予約の利用開始日
     * @return 予約リスト（該当なしは例外を送出）
     * @throws BusinessFlowClientException 該当なし
     */
    List<ReservationClientDto> findReservationByRentalItemAndStartDate(Integer rentalItemId, LocalDate startDate) throws BusinessFlowClientException;

    /**
     * 指定されたユーザが予約者の予約を取得する。
     *
     * @param reserverId 予約者のユーザID
     * @return 該当のリスト。該当なしは空リスト
     */
    List<ReservationClientDto> findReservationByReserverId(int reserverId);

    /**
     * ログインユーザが予約者の予約一覧を取得する。
     *
     * @return 該当のリスト。該当なしは空リスト
     */
    List<ReservationClientDto> getOwnReservations();

    /**
     * レンタル品の全件取得。
     * <p>
     * @return レンタル品の全件。該当なしは空リスト
     */
    List<RentalItemClientDto> getAllRentalItems();

    /**
     * ユーザの全件取得。
     * <p>
     * @return ユーザの全件。該当なしは空リスト
     */
    List<UserAccountClientDto> getAllUserAccounts();

    /**
     * レンタル品を予約する。
     * <p>
     * @param addReservationDto 登録する予約
     * @return 登録された予約（idが設定されている）
     * @throws BusinessFlowClientException 該当するレンタル品が存在しない場合、または期間が重複する予約が既に登録されている場合
     */
    ReservationClientDto addReservation(ReservationClientDto addReservationDto) throws BusinessFlowClientException;

    /**
     * レンタル品を登録する。
     * <p>
     * @param addRentalItemDto 登録レンタル品
     * @return 登録されたレンタル品（idが設定されている）
     * @throws BusinessFlowClientException 同一シリアル番号のレンタル品が既に登録されている場合
     */
    RentalItemClientDto addRentalItem(RentalItemClientDto addRentalItemDto) throws BusinessFlowClientException;

    /**
     * ユーザアカウントを登録する。
     * <p>
     * @param addUserAccountDto 登録ユーザ
     * @return 登録されたユーザアカウント（idが設定されている）
     * @throws BusinessFlowClientException 同一ログインIDのユーザが既に登録されている場合
     */
    UserAccountClientDto addUserAccount(UserAccountClientDto addUserAccountDto) throws BusinessFlowClientException;

    /**
     * 予約をキャンセルする。
     * <p>
     * @param reservationId 予約ID
     * @throws BusinessFlowClientException 該当の予約が存在しない場合
     */
    void cancelReservation(int reservationId) throws BusinessFlowClientException;

    /**
     * ユーザアカウントを更新する。
     * <p>
     * @param updateUserAccountDto 更新ユーザ
     * @return 更新後のユーザアカウント
     */
    UserAccountClientDto updateUserAccount(UserAccountClientDto updateUserAccountDto);
}