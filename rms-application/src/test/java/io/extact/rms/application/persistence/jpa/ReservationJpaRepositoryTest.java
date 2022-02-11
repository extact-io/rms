package io.extact.rms.application.persistence.jpa;

import static io.extact.rms.test.assertj.ToStringAssert.*;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.application.TestUtils;
import io.extact.rms.application.domain.Reservation;
import io.extact.rms.application.persistence.AbstractReservationRepositoryTest;
import io.extact.rms.application.persistence.ReservationRepository;
import io.extact.rms.test.junit5.JpaTransactionalExtension;
import io.extact.rms.test.junit5.TransactionalForTest;

@ExtendWith(JpaTransactionalExtension.class)
public class ReservationJpaRepositoryTest extends AbstractReservationRepositoryTest {

    private ReservationJpaRepository repository;

    @BeforeEach
    void setup(EntityManager em) {
        repository = new ReservationJpaRepository();
        TestUtils.setFieldValue(repository, "em", em);
    }

    @Test
    @TransactionalForTest
    void testAdd() {
        var addEntity = Reservation.ofTransient(LocalDateTime.of(2020, 4, 2, 10, 0, 0), LocalDateTime.of(2020, 4, 2, 12, 0, 0), "メモ4", 3, 1);
        repository.add(addEntity);

        addEntity.setId(4);
        var expect = addEntity;
        var actual = repository.get(4);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Override
    protected ReservationRepository repository() {
        return repository;
    }
}
