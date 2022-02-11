package io.extact.rms.client.api.login;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import io.extact.rms.client.api.dto.UserAccountClientDto;

/**
 * ログイン成功時にアプリケーションから通知されるイベントクラス。
 */
@Getter
@RequiredArgsConstructor
public class LoggedInEvent {

    private final UserAccountClientDto loginUser;

}
