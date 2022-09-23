package io.extact.rms.application.domain;

import java.lang.reflect.Field;

import jakarta.validation.Configuration;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import io.extact.rms.application.TestUtils;

/**
 * プロパティのsetter/getterのテストを行うテストクラスの基底クラス
 */
abstract class PropertyTest {

    /** BeanValidator */
    protected static Validator validator;

    /**
     * プロパティのContstraintのテスト用Validatorの生成
     */
    @BeforeAll
    static void initForClass() {
        Configuration<?> config = Validation.byDefaultProvider().configure();
        ValidatorFactory factory = config.buildValidatorFactory();
        validator = factory.getValidator();
        factory.close();
    }

    /**
     * テストのセットアップ。
     * テスト対象クラスの{@link Field}を解析する
     * @throws Exception エラーが発生した場合
     */
    @BeforeEach
    protected void setUp() throws Exception {
        TestUtils.inspectFieldToCache(this.getTargetClass());
    }

    /**
     * テスト対象クラスを取得する。
     * @return テスト対象クラス
     */
    protected abstract Class<?> getTargetClass();

    /**
     * 引数でしたテスト対象クラスのフィードを取得する。
     * @param fieldName フィールド名
     * @return フィールドインスタンス
     * @see #getTargetClass()
     */
    protected Field getField(String fieldName) {
        return TestUtils.getField(this.getTargetClass(), fieldName);
    }
}
