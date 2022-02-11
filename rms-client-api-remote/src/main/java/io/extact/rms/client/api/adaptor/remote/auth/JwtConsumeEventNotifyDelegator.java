package io.extact.rms.client.api.adaptor.remote.auth;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import io.extact.rms.client.api.login.JsonWebTokenConsumeEvent;

/**
 * ログイン（成功）イベントを通知するクラス。
 * MicroProfile-RestClientからClientResponseFilterを使う場合、@Injectが効かず{@link Event}が取得できない。
 * JAX-RSのProviderから直接イベント通知が行えないため、このクラスを設け通知を行うようにしている。
 */
@ApplicationScoped
public class JwtConsumeEventNotifyDelegator {

    @Inject
    Event<JsonWebTokenConsumeEvent> notificator;

    public void push(JsonWebTokenConsumeEvent event) {
        notificator.fire(event);
    }
}
