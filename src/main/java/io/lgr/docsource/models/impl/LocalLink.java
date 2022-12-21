package io.lgr.docsource.models.impl;

import io.lgr.docsource.models.Link;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalLink extends Link {
    public LocalLink(String path, Path file) {
        super(path, file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        String link = path;

        // Absolute link to root folder looking for a README.md
        if (path.equals("/")) {
            link = "/README.md";
        }

        // Markdown links can work without extension. Adding ".md" extension
        // before checking if the link exists
        if (!StringUtils.hasText(FilenameUtils.getExtension(link))) {
            link += ".md";
        }

        // If the link is absolute then check it is valid from the current directory
        // which is supposed to be the root folder
        if (link.startsWith("/")) {
            link = System.getProperty("user.dir") + link;
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
