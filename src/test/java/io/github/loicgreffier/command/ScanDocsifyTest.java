package io.github.loicgreffier.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class ScanDocsifyTest {
    String defaultUserDir = System.getProperty("user.dir");

    @BeforeEach
    void setUp() {
        System.setProperty("user.dir", defaultUserDir + "/src/test/resources/docsify");
    }

    @AfterEach
    void tearDown() {
        System.setProperty("user.dir", defaultUserDir);
    }

    @Test
    void shouldScanDocsify() {
        Scan scan = new Scan();
        scan.docsource = new Docsource();
        CommandLine cmd = new CommandLine(scan);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute("-r", ".");

        assertEquals(0, code);

        assertTrue(sw.toString().contains("Found 3 file(s) to scan"));
        assertTrue(sw.toString().contains("Success  16        5         1     22"));
        assertTrue(sw.toString().contains("Broken   7         2         1     10"));
        assertTrue(sw.toString().contains("Total    23        7         2     32"));
        assertTrue(sw.toString().contains("  - ./folder-two/page (file not found)"));
        assertTrue(sw.toString().contains("  - images/image.jpg (image not found)"));
        assertTrue(sw.toString().contains("  - /doesNotExist/folder/page (file not found)"));
        assertTrue(sw.toString().contains("  - https://www.gogle.fr/ (invalid URL)"));
        assertTrue(sw.toString().contains("  - https://www.testingmcafeesites.com/ (No subject alternative "
            + "DNS name matching www.testingmcafeesites.com found.)"));
        assertTrue(sw.toString().contains("  - ./does-not-exist (file not found)"));
        assertTrue(sw.toString().contains("  - /doesNotExist/folder-one/page (file not found)"));
        assertTrue(sw.toString().contains("  - /docsify/README (file not found)"));
        assertTrue(sw.toString().contains("  - /folder-one (file not found)"));
        assertTrue(sw.toString().contains("  - mailto:testgmail (bad format)"));
    }

    @Test
    void shouldScanDocsifyReadme() {
        Scan scan = new Scan();
        scan.docsource = new Docsource();
        CommandLine cmd = new CommandLine(scan);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute("README.md");

        assertEquals(0, code);

        assertTrue(sw.toString().contains("Success  12        5         1     18"));
        assertTrue(sw.toString().contains("Broken   4         2         1     7"));
        assertTrue(sw.toString().contains("Total    16        7         2     25"));
        assertTrue(sw.toString().contains("  - https://www.gogle.fr/ (invalid URL)"));
        assertTrue(sw.toString().contains("  - https://www.testingmcafeesites.com/ (No subject alternative "
            + "DNS name matching www.testingmcafeesites.com found.)"));
        assertTrue(sw.toString().contains("  - ./does-not-exist (file not found)"));
        assertTrue(sw.toString().contains("  - /doesNotExist/folder-one/page (file not found)"));
        assertTrue(sw.toString().contains("  - /docsify/README (file not found)"));
        assertTrue(sw.toString().contains("  - /folder-one (file not found)"));
        assertTrue(sw.toString().contains("  - mailto:testgmail (bad format)"));
    }

    @Test
    void shouldScanDocsifyPageOfFolderOne() {
        Scan scan = new Scan();
        scan.docsource = new Docsource();
        CommandLine cmd = new CommandLine(scan);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute("folder-one/page.md");

        assertEquals(0, code);

        assertTrue(sw.toString().contains("Success  4         0         0     4"));
        assertTrue(sw.toString().contains("Broken   3         0         0     3"));
        assertTrue(sw.toString().contains("Total    7         0         0     7"));
        assertTrue(sw.toString().contains("  - ./folder-two/page (file not found)"));
        assertTrue(sw.toString().contains("  - images/image.jpg (image not found)"));
        assertTrue(sw.toString().contains("  - /doesNotExist/folder/page (file not found)"));
    }

    @Test
    void shouldScanDocsifySkippingAllLinks() {
        Scan scan = new Scan();
        scan.docsource = new Docsource();
        CommandLine cmd = new CommandLine(scan);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute(
            "-r",
            "--skip-external",
            "--skip-relative",
            "--skip-mailto",
            "."
        );

        assertEquals(0, code);

        assertTrue(sw.toString().contains("Found 3 file(s) to scan"));
        assertTrue(sw.toString().contains("Success  Skipped   Skipped   Skipped  0"));
        assertTrue(sw.toString().contains("Broken   Skipped   Skipped   Skipped  0"));
        assertTrue(sw.toString().contains("Total    0         0         0        0"));
    }

    @Test
    void shouldScanDocsifyReadmeInsecure() {
        Scan scan = new Scan();
        scan.docsource = new Docsource();
        CommandLine cmd = new CommandLine(scan);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute("-r", "-k", "README.md");

        assertEquals(0, code);

        assertTrue(sw.toString().contains("Success  12        5         1     18"));
        assertTrue(sw.toString().contains("Broken   3         2         1     6"));
        assertTrue(sw.toString().contains("Total    15        7         2     24"));
        assertTrue(sw.toString().contains("  - https://www.gogle.fr/ (invalid URL)"));
        assertTrue(sw.toString().contains("  - https://www.testingmcafeesites.com/ (404)"));
        assertTrue(sw.toString().contains("  - ./does-not-exist (file not found)"));
        assertTrue(sw.toString().contains("  - /doesNotExist/folder-one/page (file not found)"));
        assertTrue(sw.toString().contains("  - /docsify/README (file not found)"));
        assertTrue(sw.toString().contains("  - mailto:testgmail (bad format)"));
    }
}
