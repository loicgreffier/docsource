package io.lgr.docsource;

import io.lgr.docsource.commands.ScanSubCommand;
import io.lgr.docsource.models.Link;
import io.lgr.docsource.models.impl.MailtoLink;
import io.lgr.docsource.models.impl.RelativeLink;
import io.lgr.docsource.models.impl.ExternalLink;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.util.List;

import static io.lgr.docsource.models.Link.Status.*;
import static org.assertj.core.api.Assertions.assertThat;

class ScanCustomTest {
    @Test
    void shouldScanCustomFolderRecursively() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-rAc=src/test/resources/custom", "-p=content", "src/test/resources/custom");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(11);
        assertThat(scanSubCommand.getScannedLinksByStatus(SUCCESS)).hasSize(7);
        assertThat(scanSubCommand.getScannedLinksByStatus(REDIRECT)).hasSize(1);

        List<Link> brokens = scanSubCommand.getScannedLinksByStatus(BROKEN);
        assertThat(brokens).hasSize(3);
        assertThat(brokens.stream().map(Link::getLink).toList()).containsAll(List.of(
                "https://www.gogle.fr/",
                "./folderTwo/does-not-exist",
                "mailto:testgmail"));

        assertThat(scanSubCommand.getScannedLinksByType(ExternalLink.class)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByType(RelativeLink.class)).hasSize(6);
        assertThat(scanSubCommand.getScannedLinksByType(MailtoLink.class)).hasSize(2);
    }

    @Test
    void shouldScanCustomFolderOneReadme() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-Ac=src/test/resources/custom", "-p=content", "src/test/resources/custom/content/folderOne/README.md");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(11);
        assertThat(scanSubCommand.getScannedLinksByStatus(SUCCESS)).hasSize(7);
        assertThat(scanSubCommand.getScannedLinksByStatus(REDIRECT)).hasSize(1);

        List<Link> brokens = scanSubCommand.getScannedLinksByStatus(BROKEN);
        assertThat(brokens).hasSize(3);
        assertThat(brokens.stream().map(Link::getLink).toList()).containsAll(List.of(
                "https://www.gogle.fr/",
                "./folderTwo/does-not-exist",
                "mailto:testgmail"));

        assertThat(scanSubCommand.getScannedLinksByType(ExternalLink.class)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByType(RelativeLink.class)).hasSize(6);
        assertThat(scanSubCommand.getScannedLinksByType(MailtoLink.class)).hasSize(2);
    }
}
