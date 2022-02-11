package io.extact.rms.client.api.login;

import org.apache.commons.lang3.tuple.Pair;

/**
 * サーバから発行された認証トークンを受信したことを通知するイベント
 */
public class JsonWebTokenConsumeEvent {

    /** サーバから受信したヘッダ(valueは受信時の生情報を入れているのでbearerも付いている) */
    private Pair<String, String> keyValue;

    public JsonWebTokenConsumeEvent(Pair<String, String> keyValue) {
        this.keyValue = keyValue;
    }

    public Pair<String, String> getToken() {
        return keyValue;
    }
}
