package io.extact.rms.client.console.login;

import java.time.LocalDateTime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.client.api.login.JsonWebTokenConsumeEvent;
import io.extact.rms.client.api.login.LoggedInEvent;

@ApplicationScoped
@Getter
@Slf4j
public class LoginEventReciever {

    private UserAccountClientDto loginUser;
    private LocalDateTime loggedInAt;
    private String jsonWebToken;


    // -----------------------------------------------------  add observer methods

    void onRecieveTokenEvent(@Observes JsonWebTokenConsumeEvent event) {
        log.debug("イベント受信 event->JsonWebTokenConsumeEvent");
        this.jsonWebToken = event.getToken().getValue();
    }

    void onLoggedInEvent(@Observes LoggedInEvent event) {
        log.debug("イベント受信 event->LoggedInEvent");
        this.loginUser = event.getLoginUser();
        this.loggedInAt = LocalDateTime.now();
    }
}
