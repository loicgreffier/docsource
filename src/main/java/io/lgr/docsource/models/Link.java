package io.lgr.docsource.models;

import lombok.Getter;
import org.fusesource.jansi.Ansi;

import java.io.File;

import static org.fusesource.jansi.Ansi.Color.*;

@Getter
public abstract class Link {
    protected final File file;
    protected final String path;
    protected final String markdown;
    protected Status status;
    protected String details;

    protected Link(File file, String markdown) {
        this.file = file;
        this.markdown = markdown;
        this.path = markdown.substring(markdown.indexOf("(") + 1, markdown.indexOf(")"));
    }

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

    public enum Status {
        SUCCESS,
        REDIRECT,
        BROKEN
    }
}
