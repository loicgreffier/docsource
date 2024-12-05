package io.github.loicgreffier.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

@Slf4j
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

        log.info(sw.toString());

        assertTrue(sw.toString().contains("Found 2 file(s) to scan"));
        assertTrue(sw.toString().contains("Success  12        3         1     16"));
        assertTrue(sw.toString().contains("Broken   3         1         1     5"));
        assertTrue(sw.toString().contains("Total    15        4         2     21"));
        assertTrue(sw.toString().contains("  - https://www.gogle.fr/ (invalid URL)"));
        assertTrue(sw.toString().contains("  - ./folder-two/does-not-exist (file not found)"));
        assertTrue(sw.toString().contains("  - content/folder-one/images/imageNotFound.jpg (image not found)"));
        assertTrue(sw.toString().contains("  - mailto:testgmail (bad format)"));
        assertTrue(sw.toString().contains("  - content/folder-one/images/imageNotFound.jpg (image not found)"));
    }

    @Test
    void shouldScanCustomSkippingExternalLinks() {
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
            "--skip-external",
            "."
        );

        assertNotEquals(0, code);

        log.info(sw.toString());

        assertTrue(sw.toString().contains("Found 2 file(s) to scan"));
        assertTrue(sw.toString().contains("Success  12        Skipped   1     13"));
        assertTrue(sw.toString().contains("Broken   3         Skipped   1     4"));
        assertTrue(sw.toString().contains("Total    15        0         2     17"));
        assertTrue(sw.toString().contains("  - ./folder-two/does-not-exist (file not found)"));
        assertTrue(sw.toString().contains("  - content/folder-one/images/imageNotFound.jpg (image not found)"));
        assertTrue(sw.toString().contains("  - mailto:testgmail (bad format)"));
        assertTrue(sw.toString().contains("  - content/folder-one/images/imageNotFound.jpg (image not found)"));
    }

    @Test
    void shouldScanCustomSkippingRelativeLinks() {
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
            "--skip-relative",
            "."
        );

        assertNotEquals(0, code);

        log.info(sw.toString());

        assertTrue(sw.toString().contains("Found 2 file(s) to scan"));
        assertTrue(sw.toString().contains("Success  Skipped   3         1     4"));
        assertTrue(sw.toString().contains("Broken   Skipped   1         1     2"));
        assertTrue(sw.toString().contains("Total    0         4         2     6"));
        assertTrue(sw.toString().contains("  - https://www.gogle.fr/ (invalid URL)"));
        assertTrue(sw.toString().contains("  - mailto:testgmail (bad format)"));
    }

    @Test
    void shouldScanCustomSkippingMailtoLinks() {
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
            "--skip-mailto",
            "."
        );

        assertNotEquals(0, code);

        log.info(sw.toString());

        assertTrue(sw.toString().contains("Found 2 file(s) to scan"));
        assertTrue(sw.toString().contains("Success  12        3         Skipped  15"));
        assertTrue(sw.toString().contains("Broken   3         1         Skipped  4"));
        assertTrue(sw.toString().contains("Total    15        4         0        19"));
        assertTrue(sw.toString().contains("  - https://www.gogle.fr/ (invalid URL)"));
        assertTrue(sw.toString().contains("  - ./folder-two/does-not-exist (file not found)"));
        assertTrue(sw.toString().contains("  - content/folder-one/images/imageNotFound.jpg (image not found)"));
        assertTrue(sw.toString().contains("  - content/folder-one/images/imageNotFound.jpg (image not found)"));
    }

    @Test
    void shouldScanCustomSkippingAllLinks() {
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
            "--skip-external",
            "--skip-relative",
            "--skip-mailto",
            "."
        );

        assertEquals(0, code);

        log.info(sw.toString());

        assertTrue(sw.toString().contains("Found 2 file(s) to scan"));
        assertTrue(sw.toString().contains("Success  Skipped   Skipped   Skipped  0"));
        assertTrue(sw.toString().contains("Broken   Skipped   Skipped   Skipped  0"));
        assertTrue(sw.toString().contains("Total    0         0         0        0"));
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
            "folder-one/page.md"
        );

        assertNotEquals(0, code);

        log.info(sw.toString());

        assertTrue(sw.toString().contains("Success  12        3         1     16"));
        assertTrue(sw.toString().contains("Broken   3         1         1     5"));
        assertTrue(sw.toString().contains("Total    15        4         2     21"));
        assertTrue(sw.toString().contains("  - https://www.gogle.fr/ (invalid URL)"));
        assertTrue(sw.toString().contains("  - ./folder-two/does-not-exist (file not found)"));
        assertTrue(sw.toString().contains("  - mailto:testgmail (bad format)"));
    }
}