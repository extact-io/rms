package io.extact.rms.application.service;

import static io.extact.rms.application.TestUtils.*;
import static io.extact.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.application.TestUtils.PathResolverParameterExtension;
import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.application.persistence.file.io.PathResolver;

@ExtendWith(PathResolverParameterExtension.class)
class RentalItemServiceTest {

    private RentalItemService service;

    @BeforeEach
    void setUp(PathResolver pathResolver) throws Exception {
        service = newRentalItemService(pathResolver);
    }

    @Test
    void testGet() {
        var expect = RentalItem.of(1, "A0001", "レンタル品1号");
        var actual = service.get(1);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testGetNull() {
        var actual = service.get(903); // 存在しないID
        assertThat(actual).isNull();
    }

    @Test
    void testFindAll() {
        var expect = List.of(
                RentalItem.of(1, "A0001", "レンタル品1号"),
                RentalItem.of(2, "A0002", "レンタル品2号"),
                RentalItem.of(3, "A0003", "レンタル品3号"),
                RentalItem.of(4, "A0004", "レンタル品4号")
            );

        List<RentalItem> actual = service.findAll();
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    void testFindBySerialNo() {
        var expect = RentalItem.of(2, "A0002", "レンタル品2号");
        var actual = service.findBySerialNo("A0002");
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFindNullBySerialNo() {
        var actual = service.findBySerialNo("A9999"); // 存在しないNo
        assertThat(actual).isNull();
    }

    @Test
    void testAdd() throws Exception {
        var expect = RentalItem.of(5, "A0005", "レンタル品5号");
        var addRentalItem = RentalItem.ofTransient("A0005", "レンタル品5号");
        var actual = service.add(addRentalItem);
        assertThatToString(actual).isEqualTo(expect);
    }
}
