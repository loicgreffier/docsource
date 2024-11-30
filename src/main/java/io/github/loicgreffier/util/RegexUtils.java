package io.github.loicgreffier.util;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * This class represents a regex utils.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegexUtils {
    /**
     * Markdown link regex.
     * Match groups like: [](http), [](https) or [](/)
     * but not like: [](#) that is not handled already or []({) that is related to Hugo.
     */
    private static final String MARKDOWN_LINK_REGEX = "!?\\[.*?\\]\\(([^#{].*?)\\)";

    /**
     * Hugo link regex.
     * Match groups like: {{< ref "http" >}}
     */
    private static final String HUGO_LINK_REGEX = "!?\\[.*?\\]\\(\\{\\{<.*ref.*\"(.*)\".*>}}\\)";

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

    /**
     * Get all regex for Markdown links.
     *
     * @return A list of regex.
     */
    public static List<String> markdownRegex() {
        return List.of(
            MARKDOWN_LINK_REGEX,
            HREF_LINK_REGEX,
            IMG_LINK_REGEX
        );
    }

    /**
     * Get all regex for Hugo links.
     *
     * @return A list of regex.
     */
    public static List<String> hugoRegex() {
        return List.of(
            MARKDOWN_LINK_REGEX,
            HUGO_LINK_REGEX,
            HREF_LINK_REGEX,
            IMG_LINK_REGEX
        );
    }
}
