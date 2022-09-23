package io.extact.rms.application.persistence;

import java.util.List;

import jakarta.validation.Valid;

import io.extact.rms.platform.validate.ValidateParam;

/**
 * 永続先に依らないリポジトリの共通操作
 *
 * @param <T> エンティティの型
 */
public interface GenericRepository<T> {

    /**
     * IDのエンティティを取得する。
     *
     * @param id ID
     * @return エンティティ。該当なしはnull
     */
    T get(int id);

    /**
     * 永続化されているエンティティを全件取得する
     *
     * @return エンティティの全件リスト。該当なしは空リスト
     */
    List<T> findAll();

    /**
     * エンティティを追加する。
     * 実装クラスもしくはメソッドに{@link ValidateParam}をアノテートすることでメソッド実行前に
     * {@link Valid}によりオブジェクトのValidationが実行される。
     *
     * @param entity エンティティ
     */
    void add(T entity);

    /**
     * エンティティを更新する。
     *
     * @param entity 更新内容
     * @return 更新後エンティティ。更新対象が存在しない場合はnull
     */
    T update(T entity);

    /**
     * エンティティを削除する。
     *
     * @param entity 削除エンティティ
     */
    void delete(T entity);

    /**
     * コンフィグ定数
     */
    static class ApiType {
        public static final String PROP_NAME ="persistence.apiType";
        public static final String FILE = "file";
        public static final String JPA = "jpa";
    }
}
