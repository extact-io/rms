package io.extact.rms.platform.util;

import static java.util.function.Predicate.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Class resource utility.
 */
public class ResourceUtils {

    /**
     * Get the class resource under the path specified by the argument by URL.
     * Search also inside the jar file.Also, if a file is specified in the argument path,
     * the URL of the corresponding file is returned.
     * The following usage example
     * <pre>
     * classpath-resources =>
     *     jar:file:/C:/foo/bar/baz.jar!/META-INF/MANIFEST.MF
     *     jar:file:/C:/foo/bar/baz.jar!/mpconfig/a.properties
     *     jar:file:/C:/foo/bar/baz.jar!/mpconfig/b.properties
     *     file:/C:/for/bar/target/classes/message.properties
     *     file:/C:/for/bar/target/classes/mpconfig/c.text
     * targetDir => mpconfig
     * return url =>
     *     jar:file:/C:/foo/bar/baz.jar!/mpconfig/a.properties
     *     jar:file:/C:/foo/bar/baz.jar!/mpconfig/b.properties
     *     file:/C:/for/bar/target/classes/mpconfig/c.text
     * </pre>
     * @param targetDir Path to search for resources. Do not add `/` at the beginning
     * @return URL of the target resource. Returns an empty list if not applicable.
     * @throws IOException IO error occurs.
     */
    public static List<URL> findResoucePathUnder(String targetDir) throws IOException {
        return findResoucePathUnder(
                    targetDir,
                    (t) -> true, // NOP filter
                    Thread.currentThread().getContextClassLoader());
    }

    /**
     * Get the class resource under the path specified by the argument by URL.
     * @see #findResoucePathUnder(String)
     */
    public static List<URL> findResoucePathUnder(String targetDir, Predicate<Object> fileFilter, ClassLoader classLoader) throws IOException {

        // TargetDir search and Split URL by protocol
        Enumeration<URL> dirs = classLoader.getResources(targetDir);
        Map<String, List<URL>> urlMap = Collections.list(dirs).stream()
                                            .collect(Collectors.groupingBy(URL::getProtocol));

        // Search under the directory for each protocol
        List<URL> targetUrls = new ArrayList<>();
        if (urlMap.containsKey("jar")) {
            List<URL> tempUrls = findResourcePathUnderInJar(targetDir, urlMap.get("jar"), fileFilter);
            targetUrls.addAll(tempUrls);
        }
        if (urlMap.containsKey("file")) {
            List<URL> tempUrls = findResourcePathUnderInFileSystem(urlMap.get("file"), fileFilter);
            targetUrls.addAll(tempUrls);
        }

        return targetUrls;
    }

    /**
     * Make a jar file on the file system based on the resource URL in the jar.
     * @param resourceUrl Resource URL in the jar.
     * @return File object in jar.
     */
    public static File toJarFileFromResourceUrl(URL resourceUrl) {
        return  toFileFromUrlString(extractFilePathStringOfUrl(resourceUrl));
    }

    /**
     * Extract the character string of the Jar file path part from the resource URL in the jar.
     * @param resourceUrl Resource URL in the jar.
     * @return String of FilePath.
     */
    public static String extractFilePathStringOfUrl(URL resourceUrl) {
        return resourceUrl.getPath().substring(0, resourceUrl.getPath().indexOf('!'));
    }


    // ----------------------------------------------------- private methods

    private static List<URL> findResourcePathUnderInJar(String targetDir, List<URL> inJarUrls, Predicate<Object> fileFilter) throws IOException {

        // --- Conversion to File ---
        // ex) targetDir = META-INF
        // input-> jar:file:/C:/foo/bar/baz.jar!/MATA-INF/...
        // step1-> file:/C:/foo/bar/baz.jar
        // step2-> new URL(file:/C:/foo/bar/baz.jar).toURI()
        // step3-> new File(uri)
        // -----------------------

        List<File> jarFiles = inJarUrls.stream()
                            .map(ResourceUtils::toJarFileFromResourceUrl)
                            .collect(Collectors.toList());


        // --- Scan in JarFile ---
        // input-> file(C:\foo\bar\baz.jar)
        // step1-> new JarFile(file).entries()
        // step2-> entry.getName() -> "META-INF/..."
        // step3-> C:\foo\bar\baz.jar -> C:/foo/bar/baz.jar
        // step4-> "jar:file:/" + "C:/foo/bar/baz.jar" + "!/" + "META-INF/..."
        // -----------------------

        List<URL> targetUrls = new ArrayList<>();

        for (File jarFile : jarFiles) {
            List<URL> urls = null;
            try (var jar = new JarFile(jarFile)) {
                urls = jar.stream()
                    .filter(not(JarEntry::isDirectory))
                    .filter(entry -> entry.getName().startsWith(targetDir))
                    .filter(fileFilter)
                    .map(entry -> {
                        String filePath = jarFile.toString().replace(File.separator, "/");
                        if (!filePath.startsWith("/")) {
                            filePath = "/" + filePath; // for Windows
                        }
                        return "jar:file:" + filePath + "!/" + entry.getName();})
                    .map(ResourceUtils::toUrl)
                    .collect(Collectors.toList());
            }
            targetUrls.addAll(urls);
        }

        return targetUrls;
    }

    private static List<URL> findResourcePathUnderInFileSystem(List<URL> inFsUrls, Predicate<Object> fileFilter) throws IOException {

        // --- Scan in classpath on FileSystem ---
        // ex) targetDir = META-INF
        // input-> file:/C:/for/bar/target/classes/META-INF
        // setp1-> scan uner "file:/C:/for/bar/target/classes/META-INF"
        // setp2-> add files that match the filters to targetUrls
        // ---------------------------------------

        List<URL> targetUrls = new ArrayList<>();

        for (var targetUrl : inFsUrls) {
            var targetPath = Paths.get(toUrl(targetUrl));
            if (Files.isDirectory(targetPath)) {
                Files.walkFileTree(targetPath, new TargetFileCollector(targetUrls, fileFilter)); // targetUrl is IN/OUT Parameter.
            } else {
                targetUrls.add(toUrl(targetPath)); // targetPath is file.
            }
        }

        return targetUrls;
    }

    private static File toFileFromUrlString(String urlSpec) {
        try {
            return new File(new URL(urlSpec).toURI());
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private static URL toUrl(String urlSpec) {
        try {
            return new URL(urlSpec);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static URL toUrl(Path path) {
        try {
            return path.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static URI toUrl(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    static class TargetFileCollector extends SimpleFileVisitor<Path> {

        private List<URL> collectHolder;
        private Predicate<Object> fileFilter;

        TargetFileCollector(List<URL> collectHolder, Predicate<Object> fileFilter) {
            this.collectHolder = collectHolder;
            this.fileFilter = fileFilter;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (fileFilter.test(file)) {
                collectHolder.add(toUrl(file));
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
