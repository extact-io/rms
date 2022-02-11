package io.extact.rms.application.integration;

import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
@AddConfig(key = "persistence.apiType", value = "jpa")
@ExtendWith(JulToSLF4DelegateExtension.class)
class IntegrationScenarioByJpaTest extends AbstractRentalReservationIntegrationScenario {

    @Override
    protected int expectedReregistrationId() {
        return 5; // DBはシーケンスなので、その時点のレコードのmax(reservation.id)とならない
    }
}
