package io.github.loicgreffier.model;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;

import java.io.File;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.fusesource.jansi.Ansi;

/**
 * This class represents a link.
 */
@Getter
@RequiredArgsConstructor
public abstract class Link {
    protected final File file;
    protected final String path;
    protected final String markdown;
    protected Status status;
    protected String details;
    @NonNull
    protected final ValidationOptions validationOptions;

    /**
     * Validate the link.
     */
    public abstract void validate();

    /**
     * Get the Ansi string representation of the link.
     *
     * @return The Ansi string representation of the link.
     */
    public String toAnsiString() {
        return "@|bold,cyan " + path + "|@ (@|bold," + getAnsiColor() + " " + details + "|@)";
    }

    /**
     * Get the Ansi color of the link.
     *
     * @return The Ansi color of the link.
     */
    private Ansi.Color getAnsiColor() {
        return switch (status) {
            case SUCCESS -> GREEN;
            case BROKEN -> RED;
        };
    }

    /**
     * This class represents the validation options.
     */
    @Getter
    @Builder
    public static class ValidationOptions {
        private String currentDir;
        private String pathPrefix;
        private boolean allAbsolute;
        private boolean skipExternal;
        private boolean skipRelative;
        private boolean skipMailto;
        private boolean insecure;
    }

    /**
     * This enum represents the link status.
     */
    public enum Status {
        SUCCESS,
        BROKEN
    }
}
