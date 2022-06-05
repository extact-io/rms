package io.extact.rms.application.persistence.file.io;

import static java.nio.file.StandardOpenOption.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import io.extact.rms.application.persistence.file.IoSystemException;
import lombok.Cleanup;

/**
 * ファイルアクセスクラス
 */
public class FileAccessor {

    /** ファイルパス */
    private Path filePath;

    // ----------------------------------------------------- constructor methods

    /**
     * コンストラクタ
     * <p>
     * @param csvFilePath ファイルパス
     */
    public FileAccessor(Path csvFilePath) {
        this.filePath = csvFilePath;
    }

    // ----------------------------------------------------- public methods

    /**
     * ファイルパスを取得する。
     * <p>
     * @return ファイルパス
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * ファイルを読み込む。
     * <p>
     * @param dataList 読み込んだデータを埋めて返す(in/out)
     * @return 読み込み件数
     * @throws IOException 読み込みエラーが発生した場合
     */
    public int load(List<String[]> dataList) throws IOException {
        @Cleanup CSVParser parser = CSVParser.parse(filePath, StandardCharsets.UTF_8, CSVFormat.RFC4180);
        parser.getRecords().stream()
                .map(record -> StreamSupport.stream(record.spliterator(), false).toList())
                .map(values -> {
                    var array = new String[values.size()];
                    values.toArray(array);
                    return array;
                })
                .forEach(dataList::add);
        return dataList.size();
    }

    /**
     * ファイルに書き込む。
     * <p>
     * @param targetData 書き込みデータ
     * @throws IOException 読み込みエラーが発生した場合
     */
    public void save(String[] targetData) throws IOException {
        List<String> singleLine = new ArrayList<>();
        singleLine.add(CSVFormat.RFC4180.format((Object[])targetData));
        Files.write(filePath, singleLine, StandardCharsets.UTF_8, WRITE, APPEND);
    }

    /**
     * 全件をファイルに書き込む。
     * ファイルに既にあるデータは削除される。
     *
     * @param allData 書き込みデータ
     * @throws IOException 読み込みエラーが発生した場合
     */
    public void saveAll(List<String[]> allData) throws IOException {
        Stream<CharSequence> allLines = allData.stream()
                .map(items -> CSVFormat.RFC4180.format((Object[]) items)); // Memory-friendly and lazy stringification
        Files.write(filePath, allLines::iterator, StandardCharsets.UTF_8, CREATE, TRUNCATE_EXISTING);
    }

    /**
     * 指定されたリソースファイルを一時ディレクトリにコピーする。
     * <p>
     * @param resourcePath リソースファイル
     * @param resolver コピー先の一時ディレクトリが指定されたPathResolver
     * @return 一時ディレクトリにコピーされたファイルのパス
     * @throws IOException ファイル入出力エラーが発生した場合
     */
    public static Path copyResourceToRealPath(String resourcePath, PathResolver resolver) {
        String[] resourcePathNodes = resourcePath.split("/");
        String outputFileName = resourcePathNodes[resourcePathNodes.length - 1];
        return copyResourceToRealPath(resourcePath, resolver, outputFileName);
    }

    public static Path copyResourceToRealPath(String resourcePath, PathResolver resolver, String outputFileName) {
        try (InputStream in = FileAccessor.class.getResourceAsStream("/" + resourcePath)) {
            if (!Files.exists(resolver.getBaseDir())) {
                Files.createDirectory(resolver.getBaseDir());
            }
            var outputFilePath = resolver.resolve(outputFileName);
            Files.copy(in, outputFilePath);
            return outputFilePath;
        } catch (IOException e) {
            throw new IoSystemException(e);
        }
    }

}
