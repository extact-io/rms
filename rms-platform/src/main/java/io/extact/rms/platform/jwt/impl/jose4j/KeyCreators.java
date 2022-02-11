package io.extact.rms.platform.jwt.impl.jose4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.function.Function;

import org.jose4j.keys.HmacKey;

class KeyCreators {
    static final Function<String, Key> PHRASE_TO_KEY_CONVERTER =
            (phrase) -> new HmacKey(phrase.getBytes(StandardCharsets.UTF_8));
}
