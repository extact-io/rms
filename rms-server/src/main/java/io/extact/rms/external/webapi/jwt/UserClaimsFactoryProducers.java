package io.extact.rms.external.webapi.jwt;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import io.extact.rms.platform.jwt.provider.JsonWebTokenGenerator.UserClaimsFactory;

@Dependent
public class UserClaimsFactoryProducers {

    @Produces
    public UserClaimsFactory createFactory() {
        return new UserDtoClaimsFactory();
    }
}
