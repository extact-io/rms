package io.extact.rms.client.api.remote;

import static org.assertj.core.api.Assertions.*;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.client.api.RentalReservationClientApi;
import io.extact.rms.client.api.dto.RentalItemClientDto;
import io.extact.rms.client.api.dto.ReservationClientDto;
import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.client.api.dto.UserAccountClientDto.ClientUserType;
import io.extact.rms.client.api.exception.ValidateClientException;
import io.extact.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
@AddConfig(key = "server.port", value = "7001")
@AddConfig(key = "configuredCdi.register.0.alias", value = "remote")
@AddConfig(key = "configuredCdi.register.1.class", value = "io.extact.rms.platform.jwt.impl.jose4j.Jose4jJwtGenerator")
@AddConfig(key = "configuredCdi.register.2.class", value = "io.extact.rms.platform.jwt.impl.jose4j.Jose4PrivateSecretedTokenValidator")
@AddConfig(key = "jwt.filter.enable", value = "true")
@TestMethodOrder(OrderAnnotation.class)
@ExtendWith(JulToSLF4DelegateExtension.class)
public class RemoteAndJpaScenarioTest extends AbstractClientScenarioTest {

    @Inject
    protected RentalReservationClientApi service;

    // JAX-RSによるRESTリソースのパラメータチェックのテスト
    // JAX-RSなのでテストはremote側のみ
    @Test
    @Order(6)
    void testClientErrorByParameter() {
        // ★:ADMINに切り替え
        service.authenticate("member2", "member2");
        catchThrowableOfType(
                () -> service.authenticate(null, null),
                ValidateClientException.class
            );

        catchThrowableOfType(
                () -> service.findReservationByRentalItemAndStartDate(0, null),
                WebApplicationException.class
            );

        catchThrowableOfType(
                () -> service.findReservationByReserverId(-1),
                ValidateClientException.class
            );

        catchThrowableOfType(
                () -> service.addReservation(ReservationClientDto.ofTransient(null, null, null, -1, -1)),
                ValidateClientException.class
            );

        catchThrowableOfType(
                () -> service.cancelReservation(-1),
                ValidateClientException.class
            );

        // ★:ADMINに切り替え
        service.authenticate("admin", "admin");
        catchThrowableOfType(
                () -> service.addRentalItem(RentalItemClientDto.ofTransient(null, null)),
                ValidateClientException.class
            );

        catchThrowableOfType(
                () -> service.addUserAccount(UserAccountClientDto.ofTransient(null, null, null, null, null, null)),
                ValidateClientException.class
            );
        catchThrowableOfType(
                () -> service.updateUserAccount(UserAccountClientDto.of(null, null, null, null, null, null, ClientUserType.ADMIN)),
                ValidateClientException.class
            );
    }

    @Override
    protected RentalReservationClientApi service() {
        return service;
    }
}
