package io.github.loicgreffier.util;


import io.github.loicgreffier.model.link.Link;
import io.github.loicgreffier.model.link.impl.ExternalLink;
import io.github.loicgreffier.model.link.impl.MailtoLink;
import io.github.loicgreffier.model.link.impl.RelativeLink;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;

/**
 * This class represents a file utils.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class FileUtils {
    private static final List<String> AUTHORIZED_EXTENSIONS = List.of("md");

    /**
     * Check if the given file extension is authorized to be processed by the application.
     *
     * @param file The file.
     * @return True if the file extension is authorized, false otherwise.
     */
    public static boolean isAuthorized(File file) {
        return AUTHORIZED_EXTENSIONS.contains(FilenameUtils.getExtension(file.toString()));
    }

    /**
     * Find files from a directory.
     *
     * @param file      The directory.
     * @param recursive True to find files recursively, false otherwise.
     * @return A list of files.
     * @throws IOException Any IO exception during file reading.
     */
    public static List<File> findFiles(File file, boolean recursive) throws IOException {
        try (Stream<Path> fileStream = Files.find(Paths.get(file.toURI()),
            recursive ? Integer.MAX_VALUE : 1,
            (filePath, fileAttr) -> fileAttr.isRegularFile() && isAuthorized(filePath.toFile()))) {
            return fileStream
                .map(Path::toFile)
                .toList();
        }
    }

    /**
     * Find links from a file.
     *
     * @param file              The file.
     * @param regexs            The regexs to find links.
     * @param validationOptions The validation options.
     * @return A list of links.
     * @throws IOException Any IO exception during file reading.
     */
    public static List<Link> findLinks(File file,
                                       List<String> regexs,
                                       Link.ValidationOptions validationOptions)
        throws IOException {
        String fileContent = Files.readString(file.toPath());
        final List<Link> links = new ArrayList<>();

        for (String regex : regexs) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(fileContent);

            while (matcher.find()) {
                // .group(0) matches all: [](...),
                // .group(1) matches the link
                if (matcher.group(0).contains("://")) {
                    if (!validationOptions.isSkipExternal()) {
                        links.add(
                            new ExternalLink(
                                file,
                                matcher.group(1),
                                matcher.group(0),
                                validationOptions
                            )
                        );
                    }
                } else if (matcher.group(0).contains("mailto:")) {
                    if (!validationOptions.isSkipMailto()) {
                        links.add(
                            new MailtoLink(
                                file,
                                matcher.group(1),
                                matcher.group(0),
                                validationOptions
                            )
                        );
                    }
                } else if (!validationOptions.isSkipRelative()) {
                    links.add(
                        new RelativeLink(
                            file,
                            matcher.group(1),
                            matcher.group(0),
                            validationOptions
                        )
                    );
                }
            }
        }

        return links;
    }

    /**
     * Is the given folder a Docsify folder.
     *
     * @param file The folder.
     * @return True if the folder is a Docsify folder, false otherwise.
     * @throws IOException Any IO exception during file reading.
     */
    public static boolean isDocsify(String file) throws IOException {
        Path indexHtml = Path.of(file + "/index.html");
        return Files.exists(indexHtml) && Files.readString(indexHtml).contains("window.$docsify");
    }

    /**
     * Is the given folder a Hugo folder.
     *
     * @param file The folder.
     * @return True if the folder is a Hugo folder, false otherwise.
     */
    public static boolean isHugo(String file) {
        return Files.exists(Path.of(file + "/hugo.yaml")) ||
            Files.exists(Path.of(file + "/hugo.toml"));
    }
}
