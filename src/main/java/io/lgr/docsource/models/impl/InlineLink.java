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
    private final String base;
    private final File pathToScan;

    public InlineLink(String base, String path, Path file, File pathToScan) {
        super(path, file);
        this.base = base;
        this.pathToScan = pathToScan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        String link = path;

        /*if (base != null && !link.startsWith("/" + Paths.get(base).getFileName())) {
            link = "/" + base + "/" + link;
        }*/

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
                link = System.getProperty("user.dir") + link;
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
