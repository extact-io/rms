package io.extact.rms.external.webapi.jwt;

import java.util.Set;

import io.extact.rms.external.webapi.dto.UserAccountResourceDto;
import io.extact.rms.platform.jwt.provider.JsonWebTokenGenerator.UserClaims;

public class UserClaimsAdaptor implements UserClaims {

    private UserAccountResourceDto org;

    UserClaimsAdaptor(UserAccountResourceDto org) {
        this.org = org;
    }

    @Override
    public String getUserId() {
        return String.valueOf(org.getId());
    }
    @Override
    public String getUserPrincipalName() {
        return org.getContact() + "@rms.com";
    }
    @Override
    public Set<String> getGroups() {
        return Set.of(org.getUserType());
    }
}
