package io.docsource.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.Color.*;

@Data
@AllArgsConstructor
public class Link {
    private String address;
    private Status status;
    private String result;
    private Type type;

    /**
     * Print a link as Ansi string
     * @return A string with Ansi format
     */
    public String toAnsiString() {
        return "@|bold,cyan " + address + "|@ (@|bold," + getAnsiColor() + " " + result + "|@)";
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

    public enum Type {
        LOCAL,
        REMOTE,
        EMAIL
    }

    public enum Status {
        SUCCESS,
        REDIRECT,
        DEAD
    }
}
