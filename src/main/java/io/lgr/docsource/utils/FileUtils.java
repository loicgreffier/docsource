package io.lgr.docsource.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
public abstract class FileUtils {
    /**
     * Markdown link regex.
     * Match groups like: [](http), [](https), [](/)
     */
    private static final String MARKDOWN_LINK_REGEX = "!?\\[.*?\\]\\((.*?)\\)";

    /**
     * Href link regex.
     * Match groups like: href=""
     */
    private static final String HREF_LINK_REGEX = "href=\\\"(.*?)\\\"";

    private static final List<String> AUTHORIZED_EXTENSIONS = List.of("md");

    private FileUtils() { }

    /**
     * Check if the format of the given file is supported
     * @param file The file
     * @return true if it is, false otherwise
     */
    public static boolean isAuthorized(File file) {
        return AUTHORIZED_EXTENSIONS.contains(FilenameUtils.getExtension(file.toString()));
    }

    /**
     * Find all files in the given directory
     * @param file The file
     * @param recursive Find files recursively or not
     * @return A list of files
     */
    public static List<File> findFiles(File file, boolean recursive) throws IOException {
        try (Stream<Path> fileStream = Files.find(Paths.get(file.toURI()), recursive ? Integer.MAX_VALUE : 1,
                (filePath, fileAttr) -> fileAttr.isRegularFile() && isAuthorized(filePath.toFile()))) {
            return fileStream
                    .map(Path::toFile)
                    .toList();
        }
    }

    /**
     * Find links from a Markdown file
     * @param file The file
     * @return A list of links
     * @throws IOException Any IO exception during file reading
     */
    public static List<String> findLinks(File file) throws IOException {
        String fileContent = Files.readString(file.toPath());
        final List<String> links = new ArrayList<>();

        for (String regex : List.of(MARKDOWN_LINK_REGEX, HREF_LINK_REGEX)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(fileContent);

            while (matcher.find()) {
                // .group(0) matches all: [](...),
                // .group(1) matches the link
                links.add(matcher.group(0));
            }
        }

        return links;
    }
}
