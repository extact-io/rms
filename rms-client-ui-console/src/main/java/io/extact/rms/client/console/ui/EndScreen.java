package io.extact.rms.client.console.ui;

import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.client.console.ui.TransitionMap.RmsScreen;
import io.extact.rms.client.console.ui.TransitionMap.Transition;

public class EndScreen implements RmsScreen {

    @Override
    public Transition play(UserAccountClientDto loginUser, boolean printHeader) {
        return null;
    }
}
