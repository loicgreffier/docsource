package io.lgr.docsource.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ScannedFile {
    private String name;
    private List<Link> links;

    /**
     * Print a link as Ansi string
     * @return A string with Ansi format
     */
    public String toAnsiString() {
        return "@|bold " + name + "|@";
    }
}
