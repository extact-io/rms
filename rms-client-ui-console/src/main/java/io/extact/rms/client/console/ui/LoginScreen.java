package io.extact.rms.client.console.ui;

import static io.extact.rms.client.console.ui.ClientConstants.*;
import static io.extact.rms.client.console.ui.textio.TextIoUtils.*;

import jakarta.enterprise.event.Event;

import lombok.RequiredArgsConstructor;

import io.extact.rms.client.api.RentalReservationClientApi;
import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.client.api.exception.BusinessFlowClientException;
import io.extact.rms.client.api.login.LoggedInEvent;
import io.extact.rms.client.console.ui.TransitionMap.RmsScreen;
import io.extact.rms.client.console.ui.TransitionMap.Transition;
import io.extact.rms.platform.env.Environment;

/**
 * アプリケーション開始画面。
 * 開始処理としてのログインを担う
 */
@RequiredArgsConstructor
public class LoginScreen implements RmsScreen {

    private final RentalReservationClientApi clientApi;
    private final Event<LoggedInEvent> loggedInEvent;

    @Override
    public Transition play(UserAccountClientDto dummy, boolean printHeader) {
        try {
            if (printHeader) {
                // 認証画面のヘッダーを表示する
                var jarInfo = Environment.getMainJarInfo();
                println("[version:" + jarInfo.getVersion() + "/build-time:" + jarInfo.getBuildtimeInfo() + "]");
                println(LOGIN_INFORMATION);
                blankLine();
            }

            var loginId = newStringInputReader()
                    .withMinLength(5)
                    .withMaxLength(10)
                    .withDefaultValue("edamame")
                    .read("ログインID");
            if (loginId.equals(SCREEN_BREAK_KEY)) {
                return Transition.END;
            }

            var password = newStringInputReader()
                    .withMinLength(5)
                    .withMaxLength(10)
                    .withDefaultValue("edamame")
                    .withInputMasking(true)
                    .read("パスワード");

            // ログイン実行
            var nowLoginUser = clientApi.authenticate(loginId, password);

            // 成功したのでログインユーザをbroadcast
            loggedInEvent.fire(new LoggedInEvent(nowLoginUser));

            return nowLoginUser.getUserType().isAdmin() ? Transition.ADMIN_MAIN : Transition.MEMBER_MAIN;

        } catch (BusinessFlowClientException e) {
            printServerError(e);
            return play(dummy, false);

        }
    }
}
