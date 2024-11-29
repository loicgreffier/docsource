package io.github.loicgreffier.command;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(code).isNotZero();

        assertThat(sw.toString()).contains("Found 2 file(s) to scan");
        assertThat(sw.toString()).contains("Success  9         3         1     13");
        assertThat(sw.toString()).contains("Broken   3         1         1     5");
        assertThat(sw.toString()).contains("Total    12        4         2     18");
        assertThat(sw.toString()).contains("  - https://www.gogle.fr/ (invalid URL)");
        assertThat(sw.toString()).contains("  - ./folder-two/does-not-exist (file not found)");
        assertThat(sw.toString()).contains(
            "  - content/folder-one/images/imageNotFound.jpg (image not found)");
        assertThat(sw.toString()).contains("  - mailto:testgmail (bad format)");
        assertThat(sw.toString()).contains(
            "  - content/folder-one/images/imageNotFound.jpg (image not found)");
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

        assertThat(code).isNotZero();

        assertThat(sw.toString()).contains("Success  9         3         1     13");
        assertThat(sw.toString()).contains("Broken   3         1         1     5");
        assertThat(sw.toString()).contains("Total    12        4         2     18");
        assertThat(sw.toString()).contains("  - https://www.gogle.fr/ (invalid URL)");
        assertThat(sw.toString()).contains("  - ./folder-two/does-not-exist (file not found)");
        assertThat(sw.toString()).contains("  - mailto:testgmail (bad format)");
    }
}
