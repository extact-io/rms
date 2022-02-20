package io.extact.rms.application.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import io.extact.rms.application.domain.Reservation;

/**
 * 予約の永続化インタフェース。
 */
public interface ReservationRepository extends GenericRepository<Reservation> {

    /**
     * レンタル品IDと利用開始日が一致する予約一覧を取得する。
     *
     * @param rentalItemId レンタル品ID
     * @param startDate 利用開始日
     * @return 該当予約。該当がない場合は空リスト
     */
    List<Reservation> findByRentalItemAndStartDate(int rentalItemId, LocalDate startDate);

    /**
     * 指定されたユーザIDが予約者の予約一覧を取得する。
     * @param reserverId 予約者のユーザID
     * @return 該当予約。該当がない場合は空リスト
     */
    List<Reservation> findByReserverId(int reserverId);

    /**
     * 指定されたレンタル品の予約一覧を取得する。
     * @param rentalItemId レンタル品ID
     * @return 該当予約。該当がない場合は空リスト
     */
    List<Reservation> findByRentalItemId(int rentalItemId);

    /**
     * 指定されたレンタル品の予約のうち、利用開始日時～利用終了日時の間に利用時間が重なっている予約を取得する
     * 該当が複数ある場合はリストの先頭の1件目を返す
     *
     * @param rentalItemId レンタル品ID
     * @param from 利用開始日時
     * @param to 利用終了日時
     * @return 予約。該当なしはnull
     */
    Reservation findOverlappedReservation(int rentalItemId, LocalDateTime from, LocalDateTime to);

    /**
     * 指定されたレンタル品の予約のうち、利用開始日時～利用終了日時の間に利用時間が重なっている予約を取得する
     *
     * @param rentalItemId レンタル品ID
     * @param from 利用開始日時
     * @param to 利用終了日時
     * @return 予約。該当がない場合は空リスト
     */
    List<Reservation> findOverlappedReservations(int rentalItemId, LocalDateTime from, LocalDateTime to);

    /**
     * 利用開始日時～利用終了日時の間に利用時間が重なっている予約を一覧で取得する
     *
     * @param from 利用開始日時
     * @param to 仕様終了日時
     * @return 該当予約。該当がない場合は空リスト
     */
    List<Reservation> findOverlappedReservations(LocalDateTime from, LocalDateTime to);
}