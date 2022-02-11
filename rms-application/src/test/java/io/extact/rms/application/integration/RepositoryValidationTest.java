package io.extact.rms.application.integration;

import static org.assertj.core.api.Assertions.*;

import javax.enterprise.inject.spi.CDI;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.application.domain.Reservation;
import io.extact.rms.application.domain.UserAccount;
import io.extact.rms.application.persistence.GenericRepository;
import io.extact.rms.application.persistence.RentalItemRepository;
import io.extact.rms.application.persistence.ReservationRepository;
import io.extact.rms.application.persistence.UserAccountRepository;
import io.extact.rms.test.assertj.ConstraintViolationSetAssert;
import io.extact.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@ExtendWith(JulToSLF4DelegateExtension.class)
public class RepositoryValidationTest {

    @Nested
    @HelidonTest(resetPerTest = true)
    @AddConfig(key = "persistence.apiType", value = "file")
    @AddConfig(key = "csv.type", value = "temporary")
    @ExtendWith(JulToSLF4DelegateExtension.class)
    class FileRepositoryValidationTest {

        @Test
        void testAddValidate() {
            testAddEntity();
        }

        @Test
        void testUpdateValidate() {
            testUpdateEntity();
        }
    }

    @Nested
    @HelidonTest(resetPerTest = true)
    @AddConfig(key = "persistence.apiType", value = "jpa")
    @ExtendWith(JulToSLF4DelegateExtension.class)
    class JpaRepositoryValidationTest {

        @Test
        void testAddValidate() {
            testAddEntity();
        }

        @Test
        void testUpdateValidate() {
            testUpdateEntity();
        }
    }

    static void testAddEntity() {
        var rentaiItemRepo = CDI.current().select(RentalItemRepository.class).get();
        testOfAddEntity(rentaiItemRepo , new RentalItem(), 1);

        var reservationRepo = CDI.current().select(ReservationRepository.class).get();
        testOfAddEntity(reservationRepo, new Reservation(), 5);

        var userAccountRepo = CDI.current().select(UserAccountRepository.class).get();
        testOfAddEntity(userAccountRepo, new UserAccount(), 4);
    }

    static void testUpdateEntity() {
        var userAccountRepo = CDI.current().select(UserAccountRepository.class).get();
        testOfUpdateUserAccount(userAccountRepo, new UserAccount(), 5);
    }

    static <T> void testOfAddEntity(GenericRepository<T> repository, T entity, int expectedErrorSize) {

        ConstraintViolationException actual =
                catchThrowableOfType(() ->
                    repository.add(entity),
                    ConstraintViolationException.class
                );
        ConstraintViolationSetAssert.assertThat(actual.getConstraintViolations())
            .hasSize(expectedErrorSize);
    }

    static void testOfUpdateUserAccount(UserAccountRepository repository, UserAccount entity, int expectedErrorSize) {

        ConstraintViolationException actual =
                catchThrowableOfType(() ->
                    repository.update(entity),
                    ConstraintViolationException.class
                );
        ConstraintViolationSetAssert.assertThat(actual.getConstraintViolations())
            .hasSize(expectedErrorSize);
    }
}
