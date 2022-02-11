package io.extact.rms.application.persistence.file;

import static io.extact.rms.application.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.application.TestUtils.PathResolverParameterExtension;
import io.extact.rms.application.domain.Reservation;
import io.extact.rms.application.persistence.AbstractReservationRepositoryTest;
import io.extact.rms.application.persistence.ReservationRepository;
import io.extact.rms.application.persistence.file.io.PathResolver;

@ExtendWith(PathResolverParameterExtension.class)
public class ReservationFileRepositoryTest extends AbstractReservationRepositoryTest {

    private ReservationFileRepository repository;

    @BeforeEach
    void setUp(PathResolver pathResolver) throws Exception {
        repository = newReservationFileRepository(pathResolver);
    }

    @Test
    void testAdd() throws Exception {
        var addReservation = Reservation.ofTransient(LocalDateTime.of(2021, 4, 18, 10, 0, 0), LocalDateTime.of(2021, 5, 16, 20, 0, 0), "メモ4", 3, 1);
        repository.add(addReservation);

        List<String[]> records = getAllRecords(repository.getStoragePath());
        String[] lastRecord = (String[]) records.get(records.size() - 1);
        String[] expectRow = { String.valueOf(records.size()), "2021/04/18 10:00", "2021/05/16 20:00", "メモ4", "3", "1" };

        assertThat(lastRecord).containsExactly(expectRow);
    }

    @Override
    protected ReservationRepository repository() {
        return repository;
    }
}
