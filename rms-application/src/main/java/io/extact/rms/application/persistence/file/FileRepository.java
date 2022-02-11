package io.extact.rms.application.persistence.file;

import java.nio.file.Path;

/**
 * ファイル固有なリポジトリ操作とデフォルト実装の定義
 *
 * @param <T> エンティティの型
 */
public interface FileRepository {

    /**
     * 永続化ファイルのパスを取得する
     *
     * @return 永続化ファイルのパス
     */
    Path getStoragePath();
}
