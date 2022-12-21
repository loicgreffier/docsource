package io.lgr.docsource;

import io.lgr.docsource.commands.ScanSubCommand;
import io.lgr.docsource.models.impl.EmailLink;
import io.lgr.docsource.models.impl.LocalLink;
import io.lgr.docsource.models.impl.RemoteLink;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static io.lgr.docsource.models.Link.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

class ScanCustomTest {
    @Test
    void shouldScanCustomFolderRecursively() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-r", "src/test/resources/custom");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(10);
        assertThat(scanSubCommand.getScannedLinksByStatus(SUCCESS)).hasSize(6);
        assertThat(scanSubCommand.getScannedLinksByStatus(DEAD)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByStatus(REDIRECT)).hasSize(1);
        assertThat(scanSubCommand.getScannedLinksByType(RemoteLink.class)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByType(LocalLink.class)).hasSize(7);
        assertThat(scanSubCommand.getScannedLinksByType(EmailLink.class)).hasSize(2);
    }

    @Test
    void shouldScanCustomFolderOneReadme() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("src/test/resources/custom/folderOne/README.md");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(10);
        assertThat(scanSubCommand.getScannedLinksByStatus(SUCCESS)).hasSize(6);
        assertThat(scanSubCommand.getScannedLinksByStatus(DEAD)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByStatus(REDIRECT)).hasSize(1);
        assertThat(scanSubCommand.getScannedLinksByType(RemoteLink.class)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByType(LocalLink.class)).hasSize(7);
        assertThat(scanSubCommand.getScannedLinksByType(EmailLink.class)).hasSize(2);
    }
}
