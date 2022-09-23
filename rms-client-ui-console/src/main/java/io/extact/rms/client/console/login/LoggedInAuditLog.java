package io.extact.rms.client.console.login;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import lombok.extern.slf4j.Slf4j;

import io.extact.rms.client.api.login.LoggedInEvent;

@ApplicationScoped
@Slf4j
public class LoggedInAuditLog {

    void onLoggedInEvent(@Observes LoggedInEvent event) {
        log.info("{}さんがログインしました！", event.getLoginUser().getUserName());
    }
}
