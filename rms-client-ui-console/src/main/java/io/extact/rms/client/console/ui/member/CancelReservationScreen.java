package io.extact.rms.client.console.ui.member;

import static io.extact.rms.client.console.ui.ClientConstants.*;
import static io.extact.rms.client.console.ui.textio.TextIoUtils.*;

import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import io.extact.rms.client.api.RentalReservationClientApi;
import io.extact.rms.client.api.dto.ReservationClientDto;
import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.client.api.exception.BusinessFlowClientException;
import io.extact.rms.client.console.ui.ClientConstants;
import io.extact.rms.client.console.ui.TransitionMap.RmsScreen;
import io.extact.rms.client.console.ui.TransitionMap.Transition;
import io.extact.rms.client.console.ui.textio.TextIoUtils;

@RequiredArgsConstructor
public class CancelReservationScreen implements RmsScreen {

    private final RentalReservationClientApi clientApi;

    @Override
    public Transition play(UserAccountClientDto loginUser, boolean printHeader) {

        if (printHeader) {
            printScreenHeader(loginUser, "レンタル品予約キャンセル画面");
        }

        var ownReservations = clientApi.getOwnReservations();

        // キャンセル可能な予約がない場合はメニューへ戻る
        if (ownReservations.isEmpty()) {
            printWarningInformation(CANNOT_CANCEL_RESERVATION_INFORMATION);
            waitPressEnter();
            return Transition.MEMBER_MAIN;
        }

        // キャンセル可能な予約一覧を表示
        println(CANCEL_RESERVATION_INFORMATION);
        ownReservations.forEach(dto ->
            println(ClientConstants.RESERVATION_FORMAT.format(dto))
        );
        blankLine();

        // キャンセルする予約を選択
        var selectedReservation = newIntInputReader()
                .withSelectableValues(
                        ownReservations.stream()
                            .map(ReservationClientDto::getId)
                            .collect(Collectors.toList()),
                        SCREEN_BREAK_VALUE)
                .read("予約番号");
        if (TextIoUtils.isBreak(selectedReservation)) {
            return Transition.MEMBER_MAIN;
        }

        blankLine();

        // レンタル品の予約キャンセルの実行
        try {
            clientApi.cancelReservation(selectedReservation);
            printResultInformation(selectedReservation);
            return Transition.MEMBER_MAIN;

        } catch (BusinessFlowClientException e) {
            printServerError(e);
            return play(loginUser, false); // start over!!

        }
    }

    private void printResultInformation(Integer selectedItem) {
        println("***** 予約キャンセル確定結果 *****");
        printf("[%s]の予約をキャンセルしました", selectedItem);
        blankLine();
        blankLine();
        waitPressEnter();
    }
}
