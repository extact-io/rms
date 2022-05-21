package io.extact.rms.application.persistence.file.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.microprofile.config.ConfigProvider;

import io.extact.rms.application.persistence.file.IoSystemException;

/**
 * ファイルパスを解決するインタフェース
 */
public interface PathResolver {

    /**
     * ファイル名をディレクトリ部も含めたパスオブジェクトに解決する
     * <p>
     * @param file ファイル名
     * @return パス
     */
    Path resolve(String file);

    /**
     * このリゾルバーインスタンスが基準としているディレクトリ部を取得
     * <p>
     * @return ディレクトリ部のパス
     */
    Path getBaseDir();


    // ----------------------------------------------------- inner class defs.

    /**
     * 指定された固定のパスを起点にパスを解決するくん。
     * デフォルトの固定パスは<code>./data</code>でシステムプロパティ<code>csv.permanent.directory</code>が
     * 指定されている場合はその指定を優先する。
     */
    public static class FixedDirPathResolver implements PathResolver {

        /** 固定バス */
        private Path baseDir;

        /**
         * コンストラクタ
         */
        public FixedDirPathResolver() {
            var config = ConfigProvider.getConfig();
            this.baseDir = Paths.get(config.getValue("csv.permanent.directory", String.class));
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public Path resolve(String file) {
            return baseDir.resolve(file);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public Path getBaseDir() {
            return this.baseDir;
        }
    }

    /**
     * OSのtmpファイルパスを起点にパスを解決するくん。
     * 生成したtempディレクトリとresolveされたファイルは{@link java.io.File#deleteOnExit()}を
     * 設定しているためJavaプロセス終了後に自動的に削除される。
     */
    public static class TempDirPathResolver implements PathResolver {

        /** 生成したtempディレクトリ */
        private Path tempDir;
        /**
         * コンストラクタ。
         * 接頭辞に"rms_"を付けたtempディレクトリを生成する。
         */
        public TempDirPathResolver() {
            try {
                this.tempDir = Files.createTempDirectory("rms_");
                this.tempDir.toFile().deleteOnExit();
            } catch (IOException e) {
               throw new IoSystemException(e);
            }
        }
        /**
         * 生成したtempディレクトリをディレクトリ部としたパスを返す。
         * @return ディレクトリ部も含めたパス
         */
        @Override
        public Path resolve(String file) {
            Path tempFile = tempDir.resolve(file);
            tempFile.toFile().deleteOnExit();
            return tempFile;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public Path getBaseDir() {
            return this.tempDir;
        }
    }
}
