package io.extact.rms.platform.jwt.consumer;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.extact.rms.platform.jwt.JwtValidateException;

public interface JsonWebTokenValidator {
    JsonWebToken validate(String token) throws JwtValidateException;
}
