package io.lgr.docsource.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.fusesource.jansi.Ansi;

import java.io.File;

import static org.fusesource.jansi.Ansi.Color.*;

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
     * Validate the status of a link
     */
    public abstract void validate();

    /**
     * Print a link as Ansi string
     * @return A string with Ansi format
     */
    public String toAnsiString() {
        return "@|bold,cyan " + path + "|@ (@|bold," + getAnsiColor() + " " + details + "|@)";
    }

    /**
     * Get an Ansi color by status
     * @return An Ansi color
     */
    private Ansi.Color getAnsiColor() {
        return switch(status) {
            case SUCCESS -> GREEN;
            case REDIRECT -> YELLOW;
            case BROKEN -> RED;
        };
    }

    @Getter
    @Builder
    public static class ValidationOptions {
        private String currentDir;
        private String pathPrefix;
        private boolean allAbsolute;
        private boolean skipExternal;
        private boolean skipRelative;
        private boolean skipMailto;
        private boolean trustAllCertificates;
    }

    public enum Status {
        SUCCESS,
        REDIRECT,
        BROKEN
    }
}
