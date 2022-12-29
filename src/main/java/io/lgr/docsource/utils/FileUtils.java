package io.lgr.docsource.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FileUtils {
    /**
     * Markdown link regex.
     * Match groups like: [](http), [](https), [](/)
     */
    private static final String MARKDOWN_LINK_REGEX = "\\[.*?\\]\\((.*?)\\)";

    /**
     * Href link regex.
     * Match groups like: href=""
     */
    private static final String HREF_LINK_REGEX = "href=\\\"(.*?)\\\"";

    /**
     * Markdown title regex.
     */
    private static final String MARKDOWN_TITLE_REGEX = "#+ .*";

    /**
     * Constructor
     */
    private FileUtils() { }

    /**
     * Get the list of authorized file extensions
     * @return A list of file extensions
     */
    public static List<String> authorizedFileExtensions() {
        return List.of("md");
    }

    /**
     * Extract links from a Markdown file
     * @param file The file
     * @return A list of links
     * @throws IOException Any IO exception during file reading
     */
    public static List<String> getLinks(Path file) throws IOException {
        String fileContent = Files.readString(file);
        final List<String> links = new ArrayList<>();

        for (String regex : List.of(MARKDOWN_LINK_REGEX, HREF_LINK_REGEX)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(fileContent);

            while (matcher.find()) {
                // .group(0) matches all: [](...),
                // .group(1) matches the link
                links.add(matcher.group(1));
            }
        }

        return links;
    }

    /**
     * Extract titles from a Markdown file
     * @param file The file
     * @return A list of titles
     * @throws IOException Any IO exception during file reading
     */
    public static List<String> getTitles(Path file) throws IOException {
        String fileContent = Files.readString(file);
        final List<String> titles = new ArrayList<>();

        Pattern pattern = Pattern.compile(MARKDOWN_TITLE_REGEX);
        Matcher matcher = pattern.matcher(fileContent);

        while (matcher.find()) {
            titles.add(matcher.group(0));
        }

        return titles;
    }

    public static List<String> getTitleIds(Path file) throws IOException {
        return getTitles(file)
                .stream()
                .map(title -> title
                        .trim()
                        .replace("# ", "#")
                        .replace("'", "")
                        .replace("[", "")
                        .replace("]", "")
                        .replaceAll("\\(.*?\\)", "")
                        .replaceAll("\\#+", "#")
                        .replace(" ", "-")
                        .toLowerCase())
                .toList();
    }
}
