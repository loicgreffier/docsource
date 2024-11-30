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
        Path checkPath = isImage() ? computeImagePath() : computePagePath();

        if (Files.exists(checkPath)) {
            status = Status.SUCCESS;
            details = "OK";
        } else {
            status = Status.BROKEN;
            details = isImage() ? "image not found" : "file not found";
        }
    }

    /**
     * Compute the image path with all the validation options.
     *
     * @return The image path.
     */
    private Path computeImagePath() {
        // If it's an image, delete the potential title
        Path checkPath = isImageInMarkdownFormat() ? Path.of(UriEncoder.decode(path.split("\\s+")[0])) :
            Path.of(UriEncoder.decode(path));

        // If the link is absolute
        if (checkPath.startsWith(File.separator)
            || validationOptions.isAllAbsolute()
            || validationOptions.isImageAbsolute()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(validationOptions.getCurrentDir());
            stringBuilder.append(File.separator);

            if (validationOptions.getImageDirectory() != null) {
                stringBuilder.append(validationOptions.getImageDirectory());
                stringBuilder.append(File.separator);
            }

            stringBuilder.append(checkPath.toString()
                .replace(".." + File.separator, "")
                .replace("." + File.separator, "")
            );
            checkPath = Path.of(stringBuilder.toString());
        } else { // If the link is relative then check it is valid from the file it belongs
            checkPath = Path.of(file.getParent() + File.separator + checkPath);
        }

        return checkPath;
    }

    /**
     * Compute the page path with all the validation options.
     *
     * @return The page path.
     */
    private Path computePagePath() {
        Path checkPath = Path.of(UriEncoder.decode(path));

        if (checkPath.toString().contains("#")) {
            checkPath = Path.of(checkPath.toString().substring(0, checkPath.toString().indexOf("#")));
        }

        // If the link is "/", look for a README file
        if (checkPath.toString().equals(File.separator)) {
            checkPath = Path.of(File.separator + "README.md");
        }

        // If the link is absolute
        if (checkPath.startsWith(File.separator) || validationOptions.isAllAbsolute()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(validationOptions.getCurrentDir());
            stringBuilder.append(File.separator);

            if (validationOptions.getContentDirectory() != null) {
                stringBuilder.append(validationOptions.getContentDirectory());
                stringBuilder.append(File.separator);
            }

            stringBuilder.append(checkPath);
            checkPath = Path.of(stringBuilder.toString());
        } else { // If the link is relative then check it is valid from the file it belongs
            checkPath = Path.of(file.getParent() + File.separator + checkPath);
        }

        if (checkPath.toFile().isDirectory()) {
            checkPath = Path.of(checkPath + File.separator + validationOptions.getIndexFilename());
        }

        // Add Markdown extension if not present already
        if (!StringUtils.hasText(FilenameUtils.getExtension(checkPath.toString()))) {
            checkPath = Path.of(checkPath + ".md");
        }

        return checkPath;
    }

    /**
     * Check if the link is an image.
     *
     * @return true if the link is an image, false otherwise.
     */
    private boolean isImage() {
        List<String> imageExtensions = List.of("jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "svg");
        return imageExtensions.contains(FilenameUtils.getExtension(path.split("\\s+")[0]));
    }

    /**
     * Check if the link is an image in Markdown format.
     *
     * @return true if the link is an image in Markdown format, false otherwise.
     */
    private boolean isImageInMarkdownFormat() {
        return isImage() && markdown.startsWith("!");
    }
}
