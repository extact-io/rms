package io.extact.rms.application.integration;

import static io.extact.rms.application.integration.IntegrationScenarioByPermanentFileTest.*;
import static org.assertj.core.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
@AddConfig(key = "persistence.apiType", value = "file")
@AddConfig(key = "csv.type", value = "permanent")
@AddConfig(key = "csv.type.permanent.init.data", value = "./target/classes/temporary")
@AddConfig(key = "csv.permanent.directory", value = TEST_PERMANENT_DIR)
@ExtendWith(JulToSLF4DelegateExtension.class)
class CopyInitDirToPermanetDirTest {
    // NOP
    @AfterAll
    static void teardownAfterAll() throws Exception {
        IntegrationScenarioByPermanentFileTest.teardownAfterAll();
    }

    @Test
    void testAssertCopyFiles() throws Exception {

        var expected = List.of(
                "rentalItemTemp.csv",
                "reservationTemp.csv",
                "userAccountTemp.csv"
                );

        List<String> fileNames = Files.list(Paths.get(TEST_PERMANENT_DIR))
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());

        assertThat(fileNames).containsExactlyInAnyOrderElementsOf(expected);
    }
}
