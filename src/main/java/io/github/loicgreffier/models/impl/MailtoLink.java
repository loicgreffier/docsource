package io.github.loicgreffier.models.impl;

import io.github.loicgreffier.models.Link;

import java.io.File;

import static io.github.loicgreffier.models.Link.Status.BROKEN;
import static io.github.loicgreffier.models.Link.Status.SUCCESS;

public class MailtoLink extends Link {
    private static final String EMAIL_REGEX = "(.+)@(.+)";

    public MailtoLink(File file, String path, String markdown, ValidationOptions validationOptions) {
        super(file, path, markdown, validationOptions);
    }

    /**
     * {@inheritDoc}
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
