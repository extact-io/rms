package io.extact.rms.application.common;

import java.util.Collections;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
@Getter
public class ServiceLoginUser {
    public static final ServiceLoginUser UNKNOWN_USER = new ServiceLoginUser(-1, Collections.emptySet());
    private final int userId;
    private final Set<String> groups;
}
