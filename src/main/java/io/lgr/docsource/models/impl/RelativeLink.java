package io.lgr.docsource.models.impl;

import io.lgr.docsource.models.Link;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.lgr.docsource.models.Link.Status.BROKEN;
import static io.lgr.docsource.models.Link.Status.SUCCESS;

public class RelativeLink extends Link {
    private final String currentDir;
    private final String startWith;

    public RelativeLink(String path, Path file, String currentDir, String startWith) {
        super(path, file);
        this.currentDir = currentDir;
        this.startWith = startWith;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        String link = path;

        if (startWith != null && !link.startsWith("/" + startWith)) {
            link = "/" + startWith + "/" + path;
        }

        // Absolute link to root folder looking for a README.md
        if (link.equals("/")) {
            link = "/README.md";
        }

        // Markdown links can work without extension. Adding ".md" extension
        // before checking if the link exists
        if (!StringUtils.hasText(FilenameUtils.getExtension(link))) {
            link += ".md";
        }

        // If the link is absolute
        if (link.startsWith("/")) {
            link = currentDir + link;
        } else { // If the link is relative then check it is valid from the file it belongs
            link = file.getParent() + "/" + link;
        }

        if (Files.exists(Paths.get(link))) {
            status = SUCCESS;
            details = "OK";
        } else {
            status = BROKEN;
            details = "file not found";
        }
    }
}
