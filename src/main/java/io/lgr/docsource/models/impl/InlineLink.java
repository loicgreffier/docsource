package io.lgr.docsource.models.impl;

import io.lgr.docsource.models.Link;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InlineLink extends Link {
    private final File pathToScan;
    private final String currentDir;
    private final String startWith;

    public InlineLink(String path, Path file, String currentDir, File pathToScan, String startWith) {
        super(path, file);
        this.currentDir = currentDir;
        this.pathToScan = pathToScan;
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
            // Check the validity from the given root directory
            if (pathToScan.isDirectory()) {
                link = pathToScan + link;
            } else { // Check the validity from the current user directory
                link = currentDir + link;
            }
        } else { // If the link is relative then check it is valid from the path of the file it belongs
            link = file.getParent() + "/" + link;
        }

        if (Files.exists(Paths.get(link))) {
            status = Status.SUCCESS;
            details = "OK";
        } else {
            status = Status.DEAD;
            details = "file not found";
        }
    }
}
