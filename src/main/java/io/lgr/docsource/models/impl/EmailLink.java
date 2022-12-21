package io.lgr.docsource.models.impl;

import io.lgr.docsource.models.Link;

import java.nio.file.Path;

public class EmailLink extends Link {
    private static final String EMAIL_REGEX = "(.+)@(.+)";

    public EmailLink(String path, Path file) {
        super(path, file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        if (path.substring(path.indexOf("mailto:")).matches(EMAIL_REGEX)) {
            status = Status.SUCCESS;
            details = "OK";
        } else {
            status = Status.DEAD;
            details = "bad format";
        }
    }
}
