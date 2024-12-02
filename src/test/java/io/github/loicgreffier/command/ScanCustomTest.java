package io.github.loicgreffier.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class ScanCustomTest {
    String defaultUserDir = System.getProperty("user.dir");

    @BeforeEach
    void setUp() {
        System.setProperty("user.dir", defaultUserDir + "/src/test/resources/custom");
    }

    @AfterEach
    void tearDown() {
        System.setProperty("user.dir", defaultUserDir);
    }

    @Test
    void shouldScanCustom() {
        Scan scan = new Scan();
        scan.docsource = new Docsource();
        CommandLine cmd = new CommandLine(scan);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute(
            "-r",
            "-A",
            "--content-directory=src/content",
            "--image-directory=src",
            "."
        );

        assertNotEquals(0, code);

        assertTrue(sw.toString().contains("Found 2 file(s) to scan"));
        assertTrue(sw.toString().contains("Success  9         3         1     13"));
        assertTrue(sw.toString().contains("Broken   3         1         1     5"));
        assertTrue(sw.toString().contains("Total    12        4         2     18"));
        assertTrue(sw.toString().contains("  - https://www.gogle.fr/ (invalid URL)"));
        assertTrue(sw.toString().contains("  - ./folder-two/does-not-exist (file not found)"));
        assertTrue(sw.toString().contains("  - content/folder-one/images/imageNotFound.jpg (image not found)"));
        assertTrue(sw.toString().contains("  - mailto:testgmail (bad format)"));
        assertTrue(sw.toString().contains("  - content/folder-one/images/imageNotFound.jpg (image not found)"));
    }

    @Test
    void shouldScanCustomPageOfFolderOne() {
        Scan scan = new Scan();
        scan.docsource = new Docsource();
        CommandLine cmd = new CommandLine(scan);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute(
            "-A",
            "--content-directory=src/content",
            "--image-directory=src",
            "src/content/folder-one/page.md"
        );

        assertEquals(0, code);

        assertTrue(sw.toString().contains("Success  9         3         1     13"));
        assertTrue(sw.toString().contains("Broken   3         1         1     5"));
        assertTrue(sw.toString().contains("Total    12        4         2     18"));
        assertTrue(sw.toString().contains("  - https://www.gogle.fr/ (invalid URL)"));
        assertTrue(sw.toString().contains("  - ./folder-two/does-not-exist (file not found)"));
        assertTrue(sw.toString().contains("  - mailto:testgmail (bad format)"));
    }
}
