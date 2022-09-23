package io.extact.rms.client.api.remote;

import jakarta.inject.Inject;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.client.api.RentalReservationClientApi;
import io.extact.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

/**
 * RemoteとLocalの共通シナリオクラスをclient-apiにおいてもtestモジュールは依存先から見ることが
 * できな。なので、remote->local(scope=test)の依存を作り、Localのテストクラスをremote側に置い
 * ている。
 */
@HelidonTest
@AddConfig(key = "configuredCdi.register.0.alias", value = "local")
@AddConfig(key = "configuredCdi.register.1.class", value = "io.extact.rms.platform.jwt.impl.jose4j.Jose4jJwtGenerator")
@AddConfig(key = "configuredCdi.register.2.class", value = "io.extact.rms.platform.jwt.impl.jose4j.Jose4PrivateSecretedTokenValidator")
@AddConfig(key = "persistence.apiType", value = "file")
@AddConfig(key = "csv.type", value = "temporary")
@ExtendWith(JulToSLF4DelegateExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class LocalAndFileScenarioTest extends AbstractClientScenarioTest {

    @Inject
    protected RentalReservationClientApi service;

    @Override
    protected RentalReservationClientApi service() {
        return service;
    }
}
