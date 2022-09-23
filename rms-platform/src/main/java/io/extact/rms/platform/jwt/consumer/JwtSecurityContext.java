package io.extact.rms.platform.jwt.consumer;

import java.security.Principal;

import jakarta.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.extact.rms.platform.stopbugs.SuppressFBWarnings;

public class JwtSecurityContext implements SecurityContext {

    @SuppressFBWarnings("URF_UNREAD_FIELD")
    private SecurityContext org; // 取りあえず持ってるだけ
    private JsonWebToken token;
    private boolean isSecure;

    JwtSecurityContext(SecurityContext org, JsonWebToken token, boolean isSecure) {
        this.org = org;
        this.token = token;
        this.isSecure = isSecure;
    }
    @Override
    public Principal getUserPrincipal() {
        return token;
    }

    @Override
    public boolean isUserInRole(String role) {
        return token.getGroups().contains(role);
    }

    @Override
    public boolean isSecure() {
        return isSecure;
    }

    @Override
    public String getAuthenticationScheme() {
        throw new UnsupportedOperationException();
    }
}
