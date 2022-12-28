package io.lgr.docsource;

import io.lgr.docsource.commands.ScanSubCommand;
import io.lgr.docsource.models.impl.MailtoLink;
import io.lgr.docsource.models.impl.RelativeLink;
import io.lgr.docsource.models.impl.ExternalLink;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static io.lgr.docsource.models.Link.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

class ScanCustomTest {
    @Test
    void shouldScanCustomFolderRecursively() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-r", "-s=content", "src/test/resources/custom");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(10);
        assertThat(scanSubCommand.getScannedLinksByStatus(SUCCESS)).hasSize(6);
        assertThat(scanSubCommand.getScannedLinksByStatus(BROKEN)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByStatus(REDIRECT)).hasSize(1);
        assertThat(scanSubCommand.getScannedLinksByType(ExternalLink.class)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByType(RelativeLink.class)).hasSize(5);
        assertThat(scanSubCommand.getScannedLinksByType(MailtoLink.class)).hasSize(2);
    }

    @Test
    void shouldScanCustomFolderOneReadme() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-c=src/test/resources/custom", "-s=content", "src/test/resources/custom/content/folderOne/README.md");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(10);
        assertThat(scanSubCommand.getScannedLinksByStatus(SUCCESS)).hasSize(6);
        assertThat(scanSubCommand.getScannedLinksByStatus(BROKEN)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByStatus(REDIRECT)).hasSize(1);
        assertThat(scanSubCommand.getScannedLinksByType(ExternalLink.class)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByType(RelativeLink.class)).hasSize(5);
        assertThat(scanSubCommand.getScannedLinksByType(MailtoLink.class)).hasSize(2);
    }
}
