package io.extact.rms.client.console.ui;

import java.time.format.DateTimeFormatter;

import io.extact.rms.client.api.dto.RentalItemClientDto;
import io.extact.rms.client.api.dto.ReservationClientDto;
import io.extact.rms.client.api.dto.UserAccountClientDto;

public class ClientConstants {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    public static final DtoFormatter<RentalItemClientDto> RENATL_ITEM_FORMAT = new DtoFormatter.RentalItemFormatter();
    public static final DtoFormatter<ReservationClientDto> RESERVATION_FORMAT = new DtoFormatter.ReservationFormatter();
    public static final DtoFormatter<UserAccountClientDto> USER_ACCOUNT_FORMAT = new DtoFormatter.UserAccountFormatter();

    public static final String SCREEN_BREAK_KEY = "-1";
    public static final int SCREEN_BREAK_VALUE = Integer.parseInt(SCREEN_BREAK_KEY);

    public static final String LOGIN_INFORMATION = "ログイン名とパスワードを入力して下さい。";
    public static final String INQUIRY_RESERVATION_INFORMATION = "レンタル品番号と日付を入力してください。" + System.lineSeparator()
        + "メニュー画面へ戻る場合は、-1を入力して下さい。";
    public static final String ENTRY_RESERVATION_INFORMATION = "レンタル品番号、利用開始日時、利用終了日時、備考(空白可)を入力してください。" + System.lineSeparator()
        + "メニュー画面へ戻る場合は、-1を入力して下さい。";
    public static final String CANCEL_RESERVATION_INFORMATION = "予約番号を入力してください。" + System.lineSeparator()
        + "メニュー画面へ戻る場合は、-1を入力して下さい。";
    public static final String CANNOT_CANCEL_RESERVATION_INFORMATION = "キャンセル可能な予約がありません。メニュー画面へ戻ります。";
    public static final String ENTRY_RENTAL_ITEM_INFORMATION = "シリアル番号、レンタル品名を入力してください。" + System.lineSeparator()
        + "管理者用メニュー画面へ戻る場合は、-1を入力して下さい。";
    public static final String ENTRY_USER_INFORMATION = "ログインID、パスワード、ユーザ名、電話番号、連絡先、権限を入力して下さい。" + System.lineSeparator()
        + "管理者用メニュー画面へ戻る場合は、-1を入力して下さい。";
    public static final String EDIT_USER_INFORMATION = "編集するユーザ番号を入力後、パスワード、ユーザ名、電話番号、連絡先、権限を入力して下さい。" + System.lineSeparator()
        + "管理者用メニュー画面へ戻る場合は、-1を入力して下さい。";

    public static final String UNKNOWN_ERROR_INFORMATION = "予期せぬエラーが発生しました。ログインからやり直してください";


}
