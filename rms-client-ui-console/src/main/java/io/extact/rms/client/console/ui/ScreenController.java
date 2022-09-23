package io.extact.rms.client.console.ui;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import io.extact.rms.client.api.RentalReservationClientApi;
import io.extact.rms.client.api.login.LoggedInEvent;
import io.extact.rms.client.console.login.LoginEventReciever;
import io.extact.rms.client.console.ui.TransitionMap.RmsScreen;
import io.extact.rms.client.console.ui.TransitionMap.Transition;
import io.extact.rms.client.console.ui.admin.AdminMainScreen;
import io.extact.rms.client.console.ui.admin.EditUserScreen;
import io.extact.rms.client.console.ui.admin.EntryRentalItemScreen;
import io.extact.rms.client.console.ui.admin.EntryUserScreen;
import io.extact.rms.client.console.ui.member.CancelReservationScreen;
import io.extact.rms.client.console.ui.member.InquiryReservationScreen;
import io.extact.rms.client.console.ui.member.MemberMainScreen;
import io.extact.rms.client.console.ui.member.ReserveRentalItemScreen;

/**
 * アプリケーションの画面遷移を制御するクラス
 */
@ApplicationScoped
public class ScreenController {

    private TransitionMap transitionMap;
    private LoginEventReciever loginEventReciever;

    @Inject
    public ScreenController(RentalReservationClientApi clientApi, Event<LoggedInEvent> event, LoginEventReciever reciever) {
        this.transitionMap = new TransitionMap();
        transitionMap.add(Transition.LOGIN, new LoginScreen(clientApi, event));
        transitionMap.add(Transition.MEMBER_MAIN, new MemberMainScreen());
        transitionMap.add(Transition.INQUIRY_RESERVATION, new InquiryReservationScreen(clientApi));
        transitionMap.add(Transition.ENTRY_RESERVATRION, new ReserveRentalItemScreen(clientApi));
        transitionMap.add(Transition.CANCEL_RESERVATRION, new CancelReservationScreen(clientApi));
        transitionMap.add(Transition.ADMIN_MAIN, new AdminMainScreen());
        transitionMap.add(Transition.ENTRY_RENTAL_ITEM, new EntryRentalItemScreen(clientApi));
        transitionMap.add(Transition.ENTRY_USER, new EntryUserScreen(clientApi));
        transitionMap.add(Transition.EDIT_USER, new EditUserScreen(clientApi));
        transitionMap.add(Transition.END, new EndScreen());
        loginEventReciever = reciever;
    }

    public void start() {
        var loginScreen = transitionMap.stratScreen();
        doPlay(loginScreen);
    }

    private RmsScreen doPlay(RmsScreen screen) {
        var next = screen.play(loginEventReciever.getLoginUser(), true);
        return next != null ? doPlay(transitionMap.nextScreen(next)) : null;
    }
}
