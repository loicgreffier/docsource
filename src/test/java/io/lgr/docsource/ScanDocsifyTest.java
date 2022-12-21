package io.lgr.docsource;

import io.lgr.docsource.commands.ScanSubCommand;
import io.lgr.docsource.models.impl.EmailLink;
import io.lgr.docsource.models.impl.LocalLink;
import io.lgr.docsource.models.impl.RemoteLink;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.File;

import static io.lgr.docsource.models.Link.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

class ScanDocsifyTest {
    @BeforeEach
    void setUp() {
        System.setProperty("user.dir", new File("src/test/resources/docsify").getAbsolutePath());
    }

    @Test
    void shouldScanDocsifyFolderRecursively() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-r", "src/test/resources/docsify");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(16);
        assertThat(scanSubCommand.getScannedLinksByStatus(SUCCESS)).hasSize(10);
        assertThat(scanSubCommand.getScannedLinksByStatus(DEAD)).hasSize(5);
        assertThat(scanSubCommand.getScannedLinksByStatus(REDIRECT)).hasSize(1);
        assertThat(scanSubCommand.getScannedLinksByType(RemoteLink.class)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByType(LocalLink.class)).hasSize(11);
        assertThat(scanSubCommand.getScannedLinksByType(EmailLink.class)).hasSize(2);
    }

    @Test
    void shouldScanDocsifyReadme() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("src/test/resources/docsify/README.md");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(12);
        assertThat(scanSubCommand.getScannedLinksByStatus(SUCCESS)).hasSize(7);
        assertThat(scanSubCommand.getScannedLinksByStatus(DEAD)).hasSize(4);
        assertThat(scanSubCommand.getScannedLinksByStatus(REDIRECT)).hasSize(1);
        assertThat(scanSubCommand.getScannedLinksByType(RemoteLink.class)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByType(LocalLink.class)).hasSize(7);
        assertThat(scanSubCommand.getScannedLinksByType(EmailLink.class)).hasSize(2);
    }

    @Test
    void shouldScanDocsifyPage() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-r", "src/test/resources/docsify/folder/page.md");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(4);
        assertThat(scanSubCommand.getScannedLinksByStatus(SUCCESS)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByStatus(DEAD)).hasSize(1);
        assertThat(scanSubCommand.getScannedLinksByStatus(REDIRECT)).isEmpty();
        assertThat(scanSubCommand.getScannedLinksByType(RemoteLink.class)).isEmpty();
        assertThat(scanSubCommand.getScannedLinksByType(LocalLink.class)).hasSize(4);
        assertThat(scanSubCommand.getScannedLinksByType(EmailLink.class)).isEmpty();
    }
}
