package io.github.loicgreffier.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

/**
 * This class represents the tests of the {@link Docsource} class.
 */
class ScanTests {
    @Test
    void shouldDisplayUsageMessage() {
        CommandLine cmd = new CommandLine(new Scan());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute();

        assertThat(code).isZero();
        assertThat(sw.toString()).contains("Usage:");
    }

    @Test
    void shouldScanDocsify() {
        Scan scan = new Scan();
        scan.docsource = new Docsource();
        CommandLine cmd = new CommandLine(scan);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute("-r", "-c=src/test/resources/docsify", "src/test/resources/docsify");

        assertThat(code).isNotZero();

        assertThat(sw.toString()).contains("Found 3 file(s) to scan");
        assertThat(sw.toString()).contains("Success  16        5         1     22");
        assertThat(sw.toString()).contains("Broken   6         2         1     9");
        assertThat(sw.toString()).contains("Total    22        7         2     31");
        assertThat(sw.toString()).contains("  - ./folder-two/page (file not found)");
        assertThat(sw.toString()).contains("  - images/image.jpg (image not found)");
        assertThat(sw.toString()).contains("  - /doesNotExist/folder/page (file not found)");
        assertThat(sw.toString()).contains("  - https://www.gogle.fr/ (invalid URL)");
        assertThat(sw.toString()).contains(
            "  - https://www.testingmcafeesites.com/ (No subject alternative DNS name matching www.testingmcafeesites.com found.)");
        assertThat(sw.toString()).contains("  - ./does-not-exist (file not found)");
        assertThat(sw.toString()).contains("  - /doesNotExist/folder-one/page (file not found)");
        assertThat(sw.toString()).contains("  - /docsify/README (file not found)");
        assertThat(sw.toString()).contains("  - mailto:testgmail (bad format)");
    }

    @Test
    void shouldScanDocsifyReadme() {
        Scan scan = new Scan();
        scan.docsource = new Docsource();
        CommandLine cmd = new CommandLine(scan);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code =
            cmd.execute("-c=src/test/resources/docsify", "src/test/resources/docsify/README.md");

        assertThat(code).isNotZero();

        assertThat(sw.toString()).contains("Success  12        5         1     18");
        assertThat(sw.toString()).contains("Broken   3         2         1     6");
        assertThat(sw.toString()).contains("Total    15        7         2     24");
        assertThat(sw.toString()).contains("  - https://www.gogle.fr/ (invalid URL)");
        assertThat(sw.toString()).contains(
            "  - https://www.testingmcafeesites.com/ (No subject alternative DNS name matching www.testingmcafeesites.com found.)");
        assertThat(sw.toString()).contains("  - ./does-not-exist (file not found)");
        assertThat(sw.toString()).contains("  - /doesNotExist/folder-one/page (file not found)");
        assertThat(sw.toString()).contains("  - /docsify/README (file not found)");
        assertThat(sw.toString()).contains("  - mailto:testgmail (bad format)");
    }

    @Test
    void shouldScanDocsifyPageOfFolderOne() {
        Scan scan = new Scan();
        scan.docsource = new Docsource();
        CommandLine cmd = new CommandLine(scan);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute("-c=src/test/resources/docsify",
            "src/test/resources/docsify/folder-one/page.md");

        assertThat(code).isNotZero();

        assertThat(sw.toString()).contains("Success  4         0         0     4");
        assertThat(sw.toString()).contains("Broken   3         0         0     3");
        assertThat(sw.toString()).contains("Total    7         0         0     7");
        assertThat(sw.toString()).contains("  - ./folder-two/page (file not found)");
        assertThat(sw.toString()).contains("  - images/image.jpg (image not found)");
        assertThat(sw.toString()).contains("  - /doesNotExist/folder/page (file not found)");
    }

    @Test
    void shouldScanDocsifySkippingAllLinks() {
        Scan scan = new Scan();
        scan.docsource = new Docsource();
        CommandLine cmd = new CommandLine(scan);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute("-r", "-c=src/test/resources/docsify",
            "--skip-external", "--skip-relative", "--skip-mailto",
            "src/test/resources/docsify");

        assertThat(code).isZero();

        assertThat(sw.toString()).contains("Found 3 file(s) to scan");
        assertThat(sw.toString()).contains("Success  Skipped   Skipped   Skipped  0");
        assertThat(sw.toString()).contains("Broken   Skipped   Skipped   Skipped  0");
        assertThat(sw.toString()).contains("Total    0         0         0        0");
    }

    @Test
    void shouldScanDocsifyReadmeInsecure() {
        Scan scan = new Scan();
        scan.docsource = new Docsource();
        CommandLine cmd = new CommandLine(scan);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute("-r", "-k", "-c=src/test/resources/docsify",
            "src/test/resources/docsify/README.md");

        assertThat(code).isNotZero();

        assertThat(sw.toString()).contains("Success  12        5         1     18");
        assertThat(sw.toString()).contains("Broken   3         2         1     6");
        assertThat(sw.toString()).contains("Total    15        7         2     24");
        assertThat(sw.toString()).contains("  - https://www.gogle.fr/ (invalid URL)");
        assertThat(sw.toString()).contains("  - https://www.testingmcafeesites.com/ (404)");
        assertThat(sw.toString()).contains("  - ./does-not-exist (file not found)");
        assertThat(sw.toString()).contains("  - /doesNotExist/folder-one/page (file not found)");
        assertThat(sw.toString()).contains("  - /docsify/README (file not found)");
        assertThat(sw.toString()).contains("  - mailto:testgmail (bad format)");
    }

    @Test
    void shouldScanCustom() {
        Scan scan = new Scan();
        scan.docsource = new Docsource();
        CommandLine cmd = new CommandLine(scan);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute("-r", "-A", "-c=src/test/resources/custom", "-p=content",
            "src/test/resources/custom");

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

        int code = cmd.execute("-A", "-c=src/test/resources/custom", "-p=content",
            "src/test/resources/custom/content/folder-one/page.md");

        assertThat(code).isNotZero();

        assertThat(sw.toString()).contains("Success  8         3         1     12");
        assertThat(sw.toString()).contains("Broken   1         1         1     3");
        assertThat(sw.toString()).contains("Total    9         4         2     15");
        assertThat(sw.toString()).contains("  - https://www.gogle.fr/ (invalid URL)");
        assertThat(sw.toString()).contains("  - ./folder-two/does-not-exist (file not found)");
        assertThat(sw.toString()).contains("  - mailto:testgmail (bad format)");
    }
}
