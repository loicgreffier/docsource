package io.lgr.docsource.models.impl;

import io.lgr.docsource.models.Link;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.lgr.docsource.models.Link.Status.BROKEN;
import static io.lgr.docsource.models.Link.Status.SUCCESS;

public class RelativeLink extends Link {
    private final String currentDir;
    private final String pathPrefix;
    private final boolean allAbsolute;

    public RelativeLink(String link, Path file, String currentDir, String pathPrefix, boolean allAbsolute) {
        super(link, file);
        this.currentDir = currentDir;
        this.pathPrefix = pathPrefix;
        this.allAbsolute = allAbsolute;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        Path path = Path.of(link);

        if (path.toString().contains("#")) {
            path = Path.of(path.toString().substring(0, path.toString().indexOf("#")));
        }

        if (pathPrefix != null && !path.getName(0).startsWith(pathPrefix)) {
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
        if (path.startsWith(File.separator) || allAbsolute) {
            path = Path.of(currentDir + File.separator + path);
        } else { // If the link is relative then check it is valid from the file it belongs
            path = Path.of(file.getParent() + File.separator + path);
        }

        computeLinkStatus(path);
    }

    /**
     * Compute the link status
     * @param path The link as path
     */
    private void computeLinkStatus(Path path) {
        if (!Files.exists(path)) {
            status = BROKEN;
            details = "file not found";
            return;
        }

        if (link.contains("#")) {
            try {
                String fileContent = Files.readString(path)
                        .toLowerCase()
                        .replace("# ", "#");

                String linkSection = link.substring(link.indexOf("#"))
                        .toLowerCase()
                        .replace("-", " ");

                if (!fileContent.contains(linkSection)) {
                    status = BROKEN;
                    details = "section not found";
                    return;
                }
            } catch (IOException e) {
                status = BROKEN;
                details = "error checking section";
                return;
            }
        }

        status = SUCCESS;
        details = "OK";
    }
}
