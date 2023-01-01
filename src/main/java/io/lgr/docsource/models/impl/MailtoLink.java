package io.lgr.docsource.models.impl;

import io.lgr.docsource.models.Link;

import java.io.File;

import static io.lgr.docsource.models.Link.Status.BROKEN;
import static io.lgr.docsource.models.Link.Status.SUCCESS;

public class MailtoLink extends Link {
    private static final String EMAIL_REGEX = "(.+)@(.+)";

    public MailtoLink(File file, String path, String markdown) {
        super(file, path, markdown);
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
