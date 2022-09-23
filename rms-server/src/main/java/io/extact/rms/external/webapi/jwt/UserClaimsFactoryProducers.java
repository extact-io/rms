package io.extact.rms.external.webapi.jwt;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

import io.extact.rms.platform.jwt.provider.JsonWebTokenGenerator.UserClaimsFactory;

@Dependent
public class UserClaimsFactoryProducers {

    @Produces
    public UserClaimsFactory createFactory() {
        return new UserDtoClaimsFactory();
    }
}
