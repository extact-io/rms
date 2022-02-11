package io.extact.rms.client.console.ui.member;

import static io.extact.rms.client.console.ui.ClientConstants.*;
import static io.extact.rms.client.console.ui.textio.TextIoUtils.*;

import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import io.extact.rms.client.api.RentalReservationClientApi;
import io.extact.rms.client.api.dto.RentalItemClientDto;
import io.extact.rms.client.api.dto.ReservationClientDto;
import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.client.api.exception.BusinessFlowClientException;
import io.extact.rms.client.console.ui.ClientConstants;
import io.extact.rms.client.console.ui.TransitionMap.RmsScreen;
import io.extact.rms.client.console.ui.TransitionMap.Transition;
import io.extact.rms.client.console.ui.textio.TextIoUtils;

@RequiredArgsConstructor
public class ReserveRentalItemScreen implements RmsScreen {

    private final RentalReservationClientApi clientApi;

    @Override
    public Transition play(UserAccountClientDto loginUser, boolean printHeader) {

        if (printHeader) {
            printScreenHeader(loginUser, "レンタル品予約画面");
        }

        // レンタル品一覧を表示
        var items = clientApi.getAllRentalItems();
        println(ENTRY_RESERVATION_INFORMATION);
        items.forEach(dto ->
            println(RENATL_ITEM_FORMAT.format(dto))
        );
        blankLine();

        // 予約するレンタル品の選択
        var selectedItem = newIntInputReader()
                .withSelectableValues(
                        items.stream()
                            .map(RentalItemClientDto::getId)
                            .collect(Collectors.toList()),
                        SCREEN_BREAK_VALUE)
                .read("レンタル品番号");
        if (TextIoUtils.isBreak(selectedItem)) {
            return Transition.MEMBER_MAIN;
        }

        // 利用開始日時の入力
        var startDateTime = newLocalDateTimeReader()
                .withFutureNow()
                .read("利用開始日時（入力例－2020/04/01 09:00）:");

        // 利用終了日時の入力
        var endDateTime = newLocalDateTimeReader()
                .withFutureThan(startDateTime)
                .read("利用終了日時（入力例－2020/04/01 18:00）:");

        // 備考の入力
        var note = newStringInputReader()
                .withMaxLength(15)
                .withDefaultValue("")
                .read("備考（空白可）");

        blankLine();

        // レンタル品予約の実行
        try {
            var addReservation = ReservationClientDto.ofTransient(startDateTime, endDateTime, note, selectedItem, loginUser.getId());
            var newReservation = clientApi.addReservation(addReservation);
            printResultInformation(newReservation);
            return Transition.MEMBER_MAIN;

        } catch (BusinessFlowClientException e) {
            printServerError(e);
            return play(loginUser, false); // start over!!

        }
    }

    private void printResultInformation(ReservationClientDto newReservation) {
        println("***** 予約確定結果 *****");
        printf(ClientConstants.RESERVATION_FORMAT.format(newReservation));
        blankLine();
        blankLine();
        waitPressEnter();
    }
}
