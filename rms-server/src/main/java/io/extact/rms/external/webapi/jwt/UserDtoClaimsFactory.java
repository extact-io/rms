package io.extact.rms.external.webapi.jwt;

import io.extact.rms.external.webapi.dto.UserAccountResourceDto;
import io.extact.rms.platform.jwt.provider.JsonWebTokenGenerator.UserClaims;
import io.extact.rms.platform.jwt.provider.JsonWebTokenGenerator.UserClaimsFactory;

public class UserDtoClaimsFactory implements UserClaimsFactory {

    @Override
    public boolean canNewInstanceFrom(Object obj) {
        return obj instanceof UserAccountResourceDto;
    }

    @Override
    public UserClaims newInstanceFrom(Object obj) {
        return new UserClaimsAdaptor((UserAccountResourceDto)obj);
    }
}
