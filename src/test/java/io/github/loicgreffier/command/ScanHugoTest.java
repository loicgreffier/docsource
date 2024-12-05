package io.github.loicgreffier.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class ScanHugoTest {
    String defaultUserDir = System.getProperty("user.dir");

    @BeforeEach
    void setUp() {
        System.setProperty("user.dir", defaultUserDir + "/src/test/resources/hugo");
    }

    @AfterEach
    void tearDown() {
        System.setProperty("user.dir", defaultUserDir);
    }

    @Test
    void shouldScanHugo() {
        Scan scan = new Scan();
        scan.docsource = new Docsource();
        CommandLine cmd = new CommandLine(scan);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute("-r", ".");

        assertEquals(0, code);

        assertTrue(sw.toString().contains("Found 4 file(s) to scan"));
        assertTrue(sw.toString().contains("Success  12        0         0     12"));
        assertTrue(sw.toString().contains("Broken   0         0         0     0"));
        assertTrue(sw.toString().contains("Total    12        0         0     12"));
    }
}
