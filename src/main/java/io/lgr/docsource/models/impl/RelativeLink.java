package io.lgr.docsource.models.impl;

import io.lgr.docsource.models.Link;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.lgr.docsource.models.Link.Status.BROKEN;
import static io.lgr.docsource.models.Link.Status.SUCCESS;

public class RelativeLink extends Link {
    private final String currentDir;
    private final String pathPrefix;
    private final boolean allAbsolute;

    public RelativeLink(File file, String markdown, String currentDir, String pathPrefix, boolean allAbsolute) {
        super(file, markdown);
        this.currentDir = currentDir;
        this.pathPrefix = pathPrefix;
        this.allAbsolute = allAbsolute;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        // If it's an image, delete the potential title
        Path checkPath = isImage() ? Path.of(path.split("\\s+")[0]) : Path.of(path);

        // If it's a link to a section, delete it
        if (checkPath.toString().contains("#")) {
            checkPath = Path.of(checkPath.toString().substring(0, checkPath.toString().indexOf("#")));
        }

        // Add the path prefix if not present already
        if (pathPrefix != null && !checkPath.getName(0).startsWith(pathPrefix)) {
            checkPath = Path.of(File.separator + pathPrefix + File.separator + checkPath);
        }

        // If the link is "/", look for a README file
        if (checkPath.toString().equals(File.separator)) {
            checkPath = Path.of(File.separator + "README.md");
        }

        // Add Markdown extension if not present already
        if (!StringUtils.hasText(FilenameUtils.getExtension(checkPath.toString()))) {
            checkPath = Path.of(checkPath + ".md");
        }

        // If the link is absolute
        if (checkPath.startsWith(File.separator) || allAbsolute) {
            checkPath = Path.of(currentDir + File.separator + checkPath);
        } else { // If the link is relative then check it is valid from the file it belongs
            checkPath = Path.of(file.getParent() + File.separator + checkPath);
        }

        if (Files.exists(checkPath)) {
            status = SUCCESS;
            details = "found";
        } else {
            status = BROKEN;
            details = isImage() ? "image not found" : "file not found";
        }
    }

    /**
     * Is a link to an image or not
     * @return true if it is, false otherwise
     */
    public boolean isImage() {
        return markdown.startsWith("!");
    }
}
