package io.extact.rms.client.console.ui;

import java.util.EnumMap;
import java.util.Map;

import io.extact.rms.client.api.dto.UserAccountClientDto;

public class TransitionMap {

    private Map<Transition, RmsScreen> transitionMap = new EnumMap<>(Transition.class);

    public enum Transition {
        LOGIN,
        MEMBER_MAIN,
        INQUIRY_RESERVATION,
        ENTRY_RESERVATRION,
        CANCEL_RESERVATRION,
        ADMIN_MAIN,
        ENTRY_RENTAL_ITEM,
        ENTRY_USER,
        EDIT_USER,
        END
    }

    public LoginScreen stratScreen() {
        return (LoginScreen) transitionMap.get(Transition.LOGIN);
    }

    public void add(Transition name, RmsScreen screen) {
        transitionMap.put(name, screen);
    }

    public RmsScreen nextScreen(Transition name) {
        return transitionMap.get(name);
    }

    public interface RmsScreen {
        Transition play(UserAccountClientDto loginUser, boolean printHeader);
    }
}
