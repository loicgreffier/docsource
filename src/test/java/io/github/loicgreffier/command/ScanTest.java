package io.github.loicgreffier.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class ScanTest {
    @Test
    void shouldDisplayUsageMessage() {
        CommandLine cmd = new CommandLine(new Scan());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute();

        assertEquals(0, code);
        assertTrue(sw.toString().contains("Usage:"));
    }
}
