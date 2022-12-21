package io.lgr.docsource.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.fusesource.jansi.Ansi;

import java.nio.file.Path;

import static org.fusesource.jansi.Ansi.Color.*;

@Getter
@RequiredArgsConstructor
public abstract class Link {
    protected final String path;
    protected final Path file;
    protected Status status;
    protected String details;

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
            case DEAD -> RED;
        };
    }

    public enum Status {
        SUCCESS,
        REDIRECT,
        DEAD
    }
}
