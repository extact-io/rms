package io.extact.rms.client.console.ui.member;

import static io.extact.rms.client.console.ui.ClientConstants.*;
import static io.extact.rms.client.console.ui.textio.TextIoUtils.*;

import java.util.List;

import io.extact.rms.client.api.RentalReservationClientApi;
import io.extact.rms.client.api.dto.RentalItemClientDto;
import io.extact.rms.client.api.dto.ReservationClientDto;
import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.client.api.exception.BusinessFlowClientException;
import io.extact.rms.client.console.ui.TransitionMap.RmsScreen;
import io.extact.rms.client.console.ui.TransitionMap.Transition;
import io.extact.rms.client.console.ui.textio.TextIoUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InquiryReservationScreen implements RmsScreen {

    private final RentalReservationClientApi clientApi;

    @Override
    public Transition play(UserAccountClientDto loginUser, boolean printHeader) {

        if (printHeader) {
            printScreenHeader(loginUser, "予約照会画面");
        }

        // レンタル品一覧を表示
        var items = clientApi.getAllRentalItems();
        println(INQUIRY_RESERVATION_INFORMATION);
        items.forEach(dto ->
            println(RENATL_ITEM_FORMAT.format(dto))
        );
        blankLine();

        // 照会するレンタル品を選択
        var selectedItem = newIntInputReader()
                .withSelectableValues(
                        items.stream()
                            .map(RentalItemClientDto::getId)
                            .toList(),
                        SCREEN_BREAK_VALUE)
                .read("レンタル品番号");
        if (TextIoUtils.isBreak(selectedItem)) {
            return Transition.MEMBER_MAIN;
        }

        // 照会する日付を入力
        var inputedDate = newLocalDateReader()
                .read("日付（入力例－2020/10/23）");

        // 照会の実行
        try {
            var results = clientApi.findReservationByRentalItemAndStartDate(selectedItem, inputedDate);
            printResultList(results);
            return Transition.MEMBER_MAIN;

        } catch (BusinessFlowClientException e) {
            printServerError(e);
            return play(loginUser, false); // start over!!

        }
    }

    private void printResultList(List<ReservationClientDto> reservations) {
        blankLine();
        println("***** 予約検索結果 *****");
        println("選択レンタル品番号：" + reservations.get(0).getRentalItemId());
        println("入力日付：" + DATE_FORMAT.format(reservations.get(0).getStartDateTime()));
        reservations.forEach(r ->
            println(RESERVATION_FORMAT.format(r))
        );
        blankLine();
        waitPressEnter();
    }
}
