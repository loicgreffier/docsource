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
    private final String pathPrefix;
    private final boolean relativeToAbsolute;

    public RelativeLink(String link, Path file, String currentDir, String pathPrefix, boolean relativeToAbsolute) {
        super(link, file);
        this.currentDir = currentDir;
        this.pathPrefix = pathPrefix;
        this.relativeToAbsolute = relativeToAbsolute;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        Path path = Path.of(link);

        if (pathPrefix != null && !path.subpath(0, 1).startsWith(pathPrefix)) {
            path = Path.of(File.separator + pathPrefix + File.separator + path);
        }

        // Absolute link to root folder looking for a README.md
        if (path.toString().equals(File.separator)) {
            path = Path.of(File.separator + "README.md");
        }

        // Markdown links can work without extension. Adding ".md" extension
        // before checking if the link exists
        if (!StringUtils.hasText(FilenameUtils.getExtension(path.toString()))) {
            path = Path.of(path + ".md");
        }

        // If the link is absolute
        if (path.startsWith(File.separator) || relativeToAbsolute) {
            path = Path.of(currentDir + File.separator + path);
        } else { // If the link is relative then check it is valid from the file it belongs
            path = Path.of(file.getParent() + File.separator + path);
        }

        if (Files.exists(path)) {
            status = SUCCESS;
            details = "OK";
        } else {
            status = BROKEN;
            details = "file not found";
        }
    }
}
