package io.extact.rms.client.console.ui.member;

import static io.extact.rms.client.console.ui.textio.TextIoUtils.*;

import lombok.RequiredArgsConstructor;

import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.client.console.ui.TransitionMap.RmsScreen;
import io.extact.rms.client.console.ui.TransitionMap.Transition;

public class MemberMainScreen implements RmsScreen {

    @RequiredArgsConstructor
    public enum MemberMenuList {

        INQUIRY("予約照会", Transition.INQUIRY_RESERVATION),
        ENTRY("レンタル品予約", Transition.ENTRY_RESERVATRION),
        CANCEL("予約キャンセル", Transition.CANCEL_RESERVATRION),
        RELOGIN("再ログイン", Transition.LOGIN),
        END("終了", Transition.END);

        private final String name;
        private final Transition transition;

        Transition getTransition() {
            return transition;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public Transition play(UserAccountClientDto loginUser, boolean printHeader) {

        printScreenHeader(loginUser, "レンタル会員サービスメニュー画面");

        var selectedMenu = newEnumInputReader(MemberMenuList.class)
                .read("メニュー番号を入力して下さい。");

        return selectedMenu.getTransition();
    }

}
