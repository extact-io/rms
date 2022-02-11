package io.extact.rms.client.api.dto;

import java.util.function.Function;

public interface Convertable {
    @SuppressWarnings("unchecked")
    default <T, R> R to(Function<T, R> func) {
        return func.apply((T) this);
    }
}
