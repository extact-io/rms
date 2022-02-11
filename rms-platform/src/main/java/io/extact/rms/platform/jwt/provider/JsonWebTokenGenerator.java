package io.extact.rms.platform.jwt.provider;

import java.util.Set;

public interface JsonWebTokenGenerator {

    String generateToken(UserClaims userClaims);

    public interface UserClaims {
        String getUserId();
        String getUserPrincipalName();
        Set<String> getGroups();
    }

    public interface UserClaimsFactory {
        boolean canNewInstanceFrom(Object obj);
        UserClaims newInstanceFrom(Object obj);
    }
}