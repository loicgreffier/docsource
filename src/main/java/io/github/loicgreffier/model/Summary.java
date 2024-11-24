package io.github.loicgreffier.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This class represents the summary of the scan.
 */
@Getter
@AllArgsConstructor
public class Summary {
    private Long successRelative;
    private Long successExternal;
    private Long successMail;
    private Long brokenRelative;
    private Long brokenExternal;
    private Long brokenMail;

    /**
     * Count the number of broken links.
     *
     * @return The number of broken links.
     */
    public Long countBrokenLinks() {
        return brokenRelative + brokenExternal + brokenMail;
    }
}
