package io.extact.rms.application;

import java.io.IOException;
import java.lang.annotation.Inherited;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import io.extact.rms.application.persistence.file.RentalItemFileRepository;
import io.extact.rms.application.persistence.file.ReservationFileRepository;
import io.extact.rms.application.persistence.file.UserAccountFileRepository;
import io.extact.rms.application.persistence.file.converter.RentalItemArrayConverter;
import io.extact.rms.application.persistence.file.converter.ReservationArrayConverter;
import io.extact.rms.application.persistence.file.converter.UserAccountArrayConverter;
import io.extact.rms.application.persistence.file.io.FileAccessor;
import io.extact.rms.application.persistence.file.io.PathResolver;
import io.extact.rms.application.service.RentalItemService;
import io.extact.rms.application.service.ReservationService;
import io.extact.rms.application.service.UserAccountService;

/**
 * テストケースで利用するユーティルクラス。
 */
public class TestUtils {

    private static final String RENTAL_ITEM_TEST_FILE_NAME = "rentalItemTest.csv";
    private static final String RESERVATION_TEST_FILE_NAME = "reservationTest.csv";
    private static final String USER_ACCOUNT_TEST_FILE_NAME = "userAccountTest.csv";


    // ----------------------------------------------------- Factory methods for Object Compose

    public static RentalItemFileRepository newRentalItemFileRepository(PathResolver pathResolver) throws IOException {
        Path tempFile =  FileAccessor.copyResourceToRealPath(RENTAL_ITEM_TEST_FILE_NAME, pathResolver);
        FileAccessor fa = new FileAccessor(tempFile);
        return new RentalItemFileRepository(fa, RentalItemArrayConverter.INSTANCE);
    }

    public static ReservationFileRepository newReservationFileRepository(PathResolver pathResolver) throws IOException {
        Path tempFile =  FileAccessor.copyResourceToRealPath(RESERVATION_TEST_FILE_NAME, pathResolver);
        FileAccessor fa = new FileAccessor(tempFile );
        return new ReservationFileRepository(fa, ReservationArrayConverter.INSTANCE);
    }

    public static UserAccountFileRepository newUserAccountFileRepository(PathResolver pathResolver) throws IOException {
        Path tempFile = FileAccessor.copyResourceToRealPath(USER_ACCOUNT_TEST_FILE_NAME, pathResolver);
        FileAccessor fa = new FileAccessor(tempFile);
        return new UserAccountFileRepository(fa, UserAccountArrayConverter.INSTANCE);
    }

    public static RentalItemService newRentalItemService(PathResolver pathResolver) throws IOException {
        return new RentalItemService(newRentalItemFileRepository(pathResolver));
    }

    public static ReservationService newReservationService(PathResolver pathResolver) throws IOException {
        return new ReservationService(newReservationFileRepository(pathResolver));
    }

    public static UserAccountService newUserAccountService(PathResolver pathResolver) throws IOException {
        return new UserAccountService(newUserAccountFileRepository(pathResolver));
    }

    public static RentalReservationApplication newRentalReservationApplication(PathResolver pathResolver)
            throws IOException {
        return new RentalReservationApplicationImpl(newRentalItemService(pathResolver),
                newReservationService(pathResolver), newUserAccountService(pathResolver));
    }


    // ----------------------------------------------------- file operate methods

    /**
     * ファイルパスで指定されたファイルの全レコードを取得する。
     *
     * @param filePath ファイルパス
     * @return 全レコード
     * @throws IOException エラーが発生した場合
     */
    public static List<String[]> getAllRecords(Path filePath) throws IOException {
        try (CSVParser parser = CSVParser.parse(filePath, StandardCharsets.UTF_8, CSVFormat.RFC4180)) {
            return parser.getRecords().stream()
                    .map(record -> StreamSupport.stream(record.spliterator(), false).collect(Collectors.toList()))
                    .map(values -> {
                        var array = new String[values.size()];
                        values.toArray(array);
                        return array;
                    })
                    .collect(Collectors.toList());
        }
    }

    /**
     * テストクラスのメソッド引数で{@link PathResolver}を指定可能するJUnit5拡張クラス実装。
     * {@link PathResolver}の実装には{@link PathResolver.TempDirPathResolver}インスタンスを返す。
     */
    public static class PathResolverParameterExtension implements ParameterResolver {
        /**
         * {@link Inherited}e
         */
        @Override
        public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
                throws ParameterResolutionException {
            return parameterContext.getParameter().getType() == PathResolver.class;
        }
        /**
         * {@link Inherited}e
         */
        @Override
        public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
                throws ParameterResolutionException {
            return new PathResolver.TempDirPathResolver();
        }
    }


    // ----------------------------------------------------- field cache methods

    /** フィールドキャッシュ */
    private static Map<Class<?>, Map<String, Field>> fieldCache = new HashMap<>();

    /**
     * 引数で渡されたクラスのフィールドを解析しキャッシュする。
     *
     * @param clazz 解析対象クラス
     */
    public static synchronized void inspectFieldToCache(Class<?> clazz) {
        if (fieldCache.containsKey(clazz)) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        Map<String, Field> fieldMap = Stream.of(fields).collect(Collectors.toMap(Field::getName, f -> f));
        fieldCache.put(clazz, fieldMap);
    }

    /**
     * 引数で指定されたフィールドをキャッシュから取得する。
     *
     * @param clazz フィールドを取得する対象のクラス
     * @param fieldName 取得するフィールド名
     * @return フィールド。該当がない場合は<code>null</code>を返す
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        return fieldCache.get(clazz).get(fieldName);
    }

    /**
     * フィールドキャッシュをクリアする。
     */
    public static synchronized void clearFieldCache() {
        fieldCache.clear();
    }


    // ----------------------------------------------------- field accessor methods

    /**
     * フィールドに値を設定する。
     *
     * @param target 値を設定するオブジェクト
     * @param fieldName フィール名
     * @param value 設定する値
     */
    public static void setFieldValue(Object target, String fieldName, Object value) {
        try {
            Field field = getNamedField(target, fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * フィールドの値を取得する。
     *
     * @param target 値を取得するオブジェクト
     * @param fieldName フィールド名
     * @return フィールドの値
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, String fieldName) {
        try {
            Field field = getNamedField(target, fieldName);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * フィールドオブジェクトを取得する。
     *
     * @param target フィールドを取得するオブジェクト
     * @param fieldName フィールド名
     * @return フィールドオブジェクト
     * @throws Exception エラーが発生した場合
     */
    public static Field getNamedField(Object target, String fieldName) throws Exception {
        return target.getClass().getDeclaredField(fieldName);
    }
}
