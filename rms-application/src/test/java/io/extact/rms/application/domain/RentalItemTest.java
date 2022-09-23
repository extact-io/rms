package io.extact.rms.application.domain;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.Set;

import jakarta.validation.ConstraintViolation;

import org.junit.jupiter.api.Test;

import io.extact.rms.application.domain.constraint.ValidationGroups.Update;
import io.extact.rms.test.assertj.ConstraintViolationSetAssert;

class RentalItemTest extends PropertyTest {

    @Override
    protected Class<?> getTargetClass() {
        return RentalItem.class;
    }

    @Test
    void testSetId() throws Exception {
        RentalItem testee = new RentalItem();
        testee.setId(100);
        Field id = this.getField("id");

        assertThat(id).isNotNull();
        assertThat(id.get(testee)).isEqualTo(100);
    }

    @Test
    void testGetId() throws Exception {
        RentalItem testee = new RentalItem();
        Field id = this.getField("id");

        assertThat(id).isNotNull();

        id.set(testee, 100);
        assertThat(testee.getId()).isEqualTo(100);
    }

    @Test
    void testSetSerialNo() throws Exception {
        RentalItem testee = new RentalItem();
        testee.setSerialNo("A0001");
        Field serialNo = this.getField("serialNo");

        assertThat(serialNo).isNotNull();
        assertThat(serialNo.get(testee)).isEqualTo("A0001");
    }

    @Test
    void testGetSerialNo() throws Exception {
        RentalItem testee = new RentalItem();
        Field serialNo = this.getField("serialNo");

        assertThat(serialNo).isNotNull();

        serialNo.set(testee, "A0001");
        assertThat(testee.getSerialNo()).isEqualTo("A0001");
    }

    @Test
    void testSetItemName() throws Exception {
        RentalItem testee = new RentalItem();
        testee.setItemName("レンタル品");
        Field itemName = this.getField("itemName");

        assertThat(itemName).isNotNull();
        assertThat(itemName.get(testee)).isEqualTo("レンタル品");
    }

    @Test
    void testGetItemName() throws Exception {
        RentalItem testee = new RentalItem();
        Field itemName = this.getField("itemName");

        assertThat(itemName).isNotNull();

        itemName.set(testee, "レンタル品");
        assertThat(testee.getItemName()).isEqualTo("レンタル品");
    }

    @Test
    void testNewInstance() {
        RentalItem testee = RentalItem.of(1, "A0001", "レンタル品");
        assertThat(testee.getId()).isEqualTo(1);
        assertThat(testee.getSerialNo()).isEqualTo("A0001");
        assertThat(testee.getItemName()).isEqualTo("レンタル品");
    }

    @Test
    void testPropetyValidation() {

        // エラーがないこと
        RentalItem ri = createAllOKRentalItem();
        Set<ConstraintViolation<RentalItem>> result = validator.validate(ri);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // IDエラー
        // -- グループ指定なし→未実行なのでエラーなし
        ri.setId(0);
        result = validator.validate(ri);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();
        // -- グループ指定あり→バリデート実行でエラー
        result = validator.validate(ri, Update.class);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("id")
            .hasMessageEndingWith("Min.message");

        // シリアル番号エラー(null)
        ri = createAllOKRentalItem();
        ri.setSerialNo(null);
        result = validator.validate(ri);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("serialNo")
            .hasMessageEndingWith("NotBlank.message");

        // シリアル番号エラー(空文字列)
        ri = createAllOKRentalItem();
        ri.setSerialNo("");
        result = validator.validate(ri);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("serialNo")
            .hasMessageEndingWith("NotBlank.message");


        // シリアル番号エラー(使用可能文字以外)
        ri = createAllOKRentalItem();
        ri.setSerialNo("A_0001");
        result = validator.validate(ri);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("serialNo")
            .hasMessageEndingWith("SerialNoCharacter.message");


        // シリアル番号エラー(15文字以内)
        ri = createAllOKRentalItem();
        ri.setSerialNo("123456789012345"); // 境界値:OK
        result = validator.validate(ri);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // シリアル番号エラー(15文字より大きい)
        ri = createAllOKRentalItem();
        ri.setSerialNo("1234567890123456"); // 境界値:NG
        result = validator.validate(ri);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("serialNo")
            .hasMessageEndingWith("Size.message");

        // レンタル品(15文字以内)
        ri = createAllOKRentalItem();
        ri.setItemName("１２３４５６７８９０１２３４５"); // 境界値:OK
        result = validator.validate(ri);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        // レンタル品(15文字より大きい)
        ri = createAllOKRentalItem();
        ri.setItemName("1234567890123456"); // 境界値:NG
        result = validator.validate(ri);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("itemName")
            .hasMessageEndingWith("Size.message");
    }

    @Test
    void testPropetyValidationForUpdate() {

        // エラーがないこと
        RentalItem rentalItem = createAllOKRentalItem();
        Set<ConstraintViolation<RentalItem>> result = validator.validate(rentalItem, Update.class);
        ConstraintViolationSetAssert.assertThat(result)
            .hasNoViolations();

        rentalItem = createAllOKRentalItem();
        rentalItem.setId(-1);
        result = validator.validate(rentalItem, Update.class);
        ConstraintViolationSetAssert.assertThat(result)
            .hasSize(1)
            .hasViolationOnPath("id")
            .hasMessageEndingWith("Min.message");
    }

    private RentalItem createAllOKRentalItem() {
        RentalItem rentalItem = new RentalItem();
        rentalItem.setId(1);
        rentalItem.setSerialNo("A-0001a");
        rentalItem.setItemName("レンタル品");
        return rentalItem;
    }
}
