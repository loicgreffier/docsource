package io.github.loicgreffier.utils;


import io.github.loicgreffier.models.Link;
import io.github.loicgreffier.models.impl.ExternalLink;
import io.github.loicgreffier.models.impl.MailtoLink;
import io.github.loicgreffier.models.impl.RelativeLink;
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
    /**
     * Markdown link regex.
     * Match groups like: [](http), [](https) or [](/)
     * but not like: [](#) or []({) that is not handled or not eligible.
     */
    private static final String MARKDOWN_LINK_REGEX = "!?\\[.*?\\]\\(([^#{].*?)\\)";

    /**
     * Href link regex.
     * Match groups like: href=""
     * Ignore href attribute containing template examples: href="${...}"
     */
    private static final String HREF_LINK_REGEX = "<a.*href=\"(?!\\$\\{)([^}]*?)(?!\\})\"";

    /**
     * Img link regex.
     * Match groups like: src=""
     * Ignore src attribute containing template examples: src="${...}"
     */
    private static final String IMG_LINK_REGEX = "<img.*src=\"(?!\\$\\{)([^\\}]*?)(?!\\})\"";

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
     * @param validationOptions The validation options.
     * @return A list of links.
     * @throws IOException Any IO exception during file reading.
     */
    public static List<Link> findLinks(File file, Link.ValidationOptions validationOptions)
        throws IOException {
        String fileContent = Files.readString(file.toPath());
        final List<Link> links = new ArrayList<>();

        for (String regex : List.of(MARKDOWN_LINK_REGEX, HREF_LINK_REGEX, IMG_LINK_REGEX)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(fileContent);

            while (matcher.find()) {
                // .group(0) matches all: [](...),
                // .group(1) matches the link
                if (matcher.group(0).contains("://")) {
                    if (!validationOptions.isSkipExternal()) {
                        links.add(new ExternalLink(file, matcher.group(1), matcher.group(0),
                            validationOptions));
                    }
                } else if (matcher.group(0).contains("mailto:")) {
                    if (!validationOptions.isSkipMailto()) {
                        links.add(new MailtoLink(file, matcher.group(1), matcher.group(0),
                            validationOptions));
                    }
                } else if (!validationOptions.isSkipRelative()) {
                    links.add(new RelativeLink(file, matcher.group(1), matcher.group(0),
                        validationOptions));
                }
            }
        }

        return links;
    }
}
