package io.github.loicgreffier.model.link.impl;

import io.github.loicgreffier.model.link.Link;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.util.UriEncoder;

/**
 * This class represents a relative link.
 */
public class RelativeLink extends Link {
    public RelativeLink(File file,
                        String path,
                        String markdown,
                        ValidationOptions validationOptions) {
        super(file, path, markdown, validationOptions);
    }

    /**
     * Validate the link.
     * If the link leads to an existing file, the link is valid.
     * If the link leads to a non-existing file, the link is broken.
     */
    @Override
    public void validate() {
        // If it's an image, delete the potential title
        Path checkPath = isImageInMarkdownFormat() ? Path.of(UriEncoder.decode(path.split("\\s+")[0])) :
            Path.of(UriEncoder.decode(path));

        // If it's a link to a section, delete it
        if (checkPath.toString().contains("#")) {
            checkPath = Path.of(checkPath.toString().substring(0, checkPath.toString().indexOf("#")));
        }

        // Add the path prefix if not present already
        /*if (validationOptions.getContentPath() != null
            && !checkPath.getName(0).startsWith(validationOptions.getContentPath())) {
            checkPath = Path.of(File.separator
                + validationOptions.getContentPath()
                + File.separator
                + checkPath);
        }*/

        // If the link is "/", look for a README file
        if (checkPath.toString().equals(File.separator)) {
            checkPath = Path.of(File.separator + "README.md");
        }

        // Add Markdown extension if not present already
        if (!StringUtils.hasText(FilenameUtils.getExtension(checkPath.toString()))) {
            checkPath = Path.of(checkPath + ".md");
        }

        // If the link is absolute
        if (checkPath.startsWith(File.separator) || validationOptions.isAllAbsolute()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(validationOptions.getCurrentDir());
            stringBuilder.append(File.separator);
            if (isImage() && validationOptions.getImageDirectory() != null) {
                stringBuilder.append(validationOptions.getImageDirectory());
                stringBuilder.append(File.separator);
            } else {
                if (validationOptions.getContentDirectory() != null) {
                    stringBuilder.append(validationOptions.getContentDirectory());
                    stringBuilder.append(File.separator);
                }
            }
            stringBuilder.append(checkPath);
            checkPath = Path.of(stringBuilder.toString());
        } else { // If the link is relative then check it is valid from the file it belongs
            checkPath = Path.of(file.getParent() + File.separator + checkPath);
        }

        if (Files.exists(checkPath)) {
            status = Status.SUCCESS;
            details = "OK";
        } else {
            status = Status.BROKEN;
            details = isImage() ? "image not found" : "file not found";
        }
    }

    /**
     * Check if the link is an image.
     *
     * @return true if the link is an image, false otherwise.
     */
    public boolean isImage() {
        List<String> imageExtensions = List.of("jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "svg");
        return imageExtensions.contains(FilenameUtils.getExtension(path.split("\\s+")[0]));
    }

    /**
     * Check if the link is an image in Markdown format.
     *
     * @return true if the link is an image in Markdown format, false otherwise.
     */
    public boolean isImageInMarkdownFormat() {
        return isImage() && markdown.startsWith("!");
    }
}
