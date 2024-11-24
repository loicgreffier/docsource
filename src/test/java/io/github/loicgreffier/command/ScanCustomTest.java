package io.github.loicgreffier.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class ScanCustomTest {

    @BeforeAll
    static void setUp() {
        System.setProperty("user.dir", "src/test/resources/custom");
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
            "-p=content",
            "src/test/resources/custom"
        );

        assertThat(code).isNotZero();

        assertThat(sw.toString()).contains("Found 2 file(s) to scan");
        assertThat(sw.toString()).contains("Success  8         3         1     12");
        assertThat(sw.toString()).contains("Broken   1         1         1     3");
        assertThat(sw.toString()).contains("Total    9         4         2     15");
        assertThat(sw.toString()).contains("  - https://www.gogle.fr/ (invalid URL)");
        assertThat(sw.toString()).contains("  - ./folder-two/does-not-exist (file not found)");
        assertThat(sw.toString()).contains("  - mailto:testgmail (bad format)");
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
            "-p=content",
            "src/test/resources/custom/content/folder-one/page.md"
        );

        assertThat(code).isNotZero();

        assertThat(sw.toString()).contains("Success  8         3         1     12");
        assertThat(sw.toString()).contains("Broken   1         1         1     3");
        assertThat(sw.toString()).contains("Total    9         4         2     15");
        assertThat(sw.toString()).contains("  - https://www.gogle.fr/ (invalid URL)");
        assertThat(sw.toString()).contains("  - ./folder-two/does-not-exist (file not found)");
        assertThat(sw.toString()).contains("  - mailto:testgmail (bad format)");
    }
}
