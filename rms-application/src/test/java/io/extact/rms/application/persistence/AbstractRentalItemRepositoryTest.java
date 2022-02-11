package io.extact.rms.application.persistence;

import static io.extact.rms.test.assertj.ToStringAssert.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.extact.rms.application.domain.RentalItem;
import io.extact.rms.test.junit5.TransactionalForTest;

public abstract class AbstractRentalItemRepositoryTest {

    protected abstract RentalItemRepository repository();

    @Test
    void testGet() {
        var expect = RentalItem.of(1, "A0001", "レンタル品1号");
        var actual = repository().get(1);
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFindAll() {
        var expect = List.of(
                RentalItem.of(1, "A0001", "レンタル品1号"),
                RentalItem.of(2, "A0002", "レンタル品2号"),
                RentalItem.of(3, "A0003", "レンタル品3号"),
                RentalItem.of(4, "A0004", "レンタル品4号"));
        var actual = repository().findAll();
        assertThatToString(actual).containsExactlyElementsOf(expect);
    }

    @Test
    void testFindBySerialNo() {
        var expect = RentalItem.of(3, "A0003", "レンタル品3号");
        var actual = repository().findBySerialNo("A0003");
        assertThatToString(actual).isEqualTo(expect);
    }

    @Test
    void testFindBySerialNoNotFound() {
        var actual = repository().findBySerialNo("A9999");
        assertThat(actual).isNull();
    }

    @Test
    @TransactionalForTest
    void testUpate() {
        var expected = RentalItem.of(3, "updateSN", "updateName");
        var updateRentalItem = repository().get(3);
        updateRentalItem.setSerialNo("updateSN");
        updateRentalItem.setItemName("updateName");
        var actual = repository().update(updateRentalItem);
        assertThatToString(actual).isEqualTo(expected);
    }

    @Test
    @TransactionalForTest
    void testDelete() {
        // 削除実行
        var deleteRentalItem = repository().get(2);
        repository().delete(deleteRentalItem);
        // 削除後ファイルの取得
        var rentalItems = this.repository().findAll();
        // 検証
        var expected = List.of(
                RentalItem.of(1, "A0001", "レンタル品1号"),
                RentalItem.of(3, "A0003", "レンタル品3号"),
                RentalItem.of(4, "A0004", "レンタル品4号"));
        assertThatToString(rentalItems).containsExactlyElementsOf(expected);
    }
}
