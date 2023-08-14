package io.github.loicgreffier.models.impl;

import static io.github.loicgreffier.models.Link.Status.BROKEN;
import static io.github.loicgreffier.models.Link.Status.SUCCESS;

import io.github.loicgreffier.models.Link;
import java.io.File;

/**
 * This class represents a mailto link.
 */
public class MailtoLink extends Link {
    private static final String EMAIL_REGEX = "(.+)@(.+)";

    public MailtoLink(File file, String path, String markdown,
                      ValidationOptions validationOptions) {
        super(file, path, markdown, validationOptions);
    }

    /**
     * Validate the link.
     * Check if the link is a valid email address.
     * If the link is a valid email address, the link is valid.
     * If the link is not a valid email address, the link is broken.
     */
    @Override
    public void validate() {
        if (path.substring(path.indexOf("mailto:")).matches(EMAIL_REGEX)) {
            status = SUCCESS;
            details = "OK";
        } else {
            status = BROKEN;
            details = "bad format";
        }
    }
}
