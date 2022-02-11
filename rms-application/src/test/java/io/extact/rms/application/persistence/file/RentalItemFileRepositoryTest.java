package io.extact.rms.application.persistence.file;

import static io.extact.rms.application.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.application.TestUtils.PathResolverParameterExtension;
import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.application.persistence.AbstractRentalItemRepositoryTest;
import io.extact.rms.application.persistence.RentalItemRepository;
import io.extact.rms.application.persistence.file.io.PathResolver;

@ExtendWith(PathResolverParameterExtension.class)
public class RentalItemFileRepositoryTest extends AbstractRentalItemRepositoryTest {

    private RentalItemFileRepository repository;

    @BeforeEach
    void setUp(PathResolver pathResolver) throws Exception {
        repository = newRentalItemFileRepository(pathResolver);
    }

    @Test
    void testAdd() throws Exception {

        RentalItem addRentalItem = RentalItem.ofTransient("A0005", "レンタル品5号");

        repository.add(addRentalItem);

        List<String[]> records = getAllRecords(repository.getStoragePath());
        String[] lastRecord = (String[]) records.get(records.size() - 1);
        String[] expectRow = { "-1", "A0005", "レンタル品5号" };
        expectRow[0] = String.valueOf(records.size());

        assertThat(lastRecord).containsExactly(expectRow);
    }

    @Override
    protected RentalItemRepository repository() {
        return repository;
    }
}
