package io.extact.rms.client.api.remote;

import static org.assertj.core.api.Assertions.*;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.client.api.RentalReservationClientApi;
import io.extact.rms.client.api.adaptor.remote.auth.SecurityConstraintClientException;
import io.extact.rms.client.api.dto.RentalItemClientDto;
import io.extact.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
@AddConfig(key = "server.port", value = "7001")
@AddConfig(key = "configuredCdi.register.0.alias", value = "remote")
@AddConfig(key = "configuredCdi.register.1.class", value = "io.extact.rms.platform.jwt.impl.jose4j.Jose4jJwtGenerator")
@AddConfig(key = "configuredCdi.register.2.class", value = "io.extact.rms.platform.jwt.impl.jose4j.Jose4PrivateSecretedTokenValidator")
@AddConfig(key = "jwt.filter.enable", value = "true")
@ExtendWith(JulToSLF4DelegateExtension.class)
public class RemoteSecurityScenarioTest {

    @Inject
    private RentalReservationClientApi service;

    @Test
    void testClientSecurityScenario() {

        // @Authenticatedのメソッドが認証前でエラーになること
        SecurityConstraintClientException actual = catchThrowableOfType(
            () -> service.getAllRentalItems(),
            SecurityConstraintClientException.class
        );
        assertThat(actual.getErrorStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());


        // 認証後に正常に呼び出せること
        service.authenticate("member1", "member1");
        service.getAllRentalItems();

        // @RollAllowedの権限がないメソッドが認可エラーになること
        actual = catchThrowableOfType(
            () -> service.addRentalItem(RentalItemClientDto.ofTransient("1234", "foo")),
            SecurityConstraintClientException.class
        );
        assertThat(actual.getErrorStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        // 権限があるユーザにログインしなおして正常に呼び出せること
        service.authenticate("admin", "admin");
        service.addRentalItem(RentalItemClientDto.ofTransient("1234", "foo"));
    }
}
