package io.extact.rms.platform.jwt;

public class JwtValidateException extends Exception {
    public JwtValidateException(Exception e) {
        super(e);
    }
}
