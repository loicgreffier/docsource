package io.lgr.docsource;

import io.lgr.docsource.commands.ScanSubCommand;
import io.lgr.docsource.models.impl.EmailLink;
import io.lgr.docsource.models.impl.InlineLink;
import io.lgr.docsource.models.impl.ExternalLink;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.File;

import static io.lgr.docsource.models.Link.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

class ScanCustomTest {
    @BeforeEach
    void setUp() {
        System.setProperty("user.dir", new File("src/test/resources/custom/").getAbsolutePath());
    }

    @Test
    void shouldScanCustomFolderRecursively() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-rb=content", "src/test/resources/custom/");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(10);
        assertThat(scanSubCommand.getScannedLinksByStatus(SUCCESS)).hasSize(6);
        assertThat(scanSubCommand.getScannedLinksByStatus(DEAD)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByStatus(REDIRECT)).hasSize(1);
        assertThat(scanSubCommand.getScannedLinksByType(ExternalLink.class)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByType(InlineLink.class)).hasSize(5);
        assertThat(scanSubCommand.getScannedLinksByType(EmailLink.class)).hasSize(2);
    }

    @Test
    void shouldScanCustomFolderOneReadme() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-b=content", "src/test/resources/custom/content/folderOne/README.md");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(10);
        assertThat(scanSubCommand.getScannedLinksByStatus(SUCCESS)).hasSize(6);
        assertThat(scanSubCommand.getScannedLinksByStatus(DEAD)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByStatus(REDIRECT)).hasSize(1);
        assertThat(scanSubCommand.getScannedLinksByType(ExternalLink.class)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByType(InlineLink.class)).hasSize(5);
        assertThat(scanSubCommand.getScannedLinksByType(EmailLink.class)).hasSize(2);
    }
}
