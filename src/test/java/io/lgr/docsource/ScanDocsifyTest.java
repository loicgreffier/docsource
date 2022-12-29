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

class ScanDocsifyTest {
    @Test
    void shouldScanDocsifyFolderRecursively() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-rc=src/test/resources/docsify", "src/test/resources/docsify");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(25);
        assertThat(scanSubCommand.getScannedLinksByStatus(SUCCESS)).hasSize(15);
        assertThat(scanSubCommand.getScannedLinksByStatus(REDIRECT)).hasSize(1);

        List<Link> brokens = scanSubCommand.getScannedLinksByStatus(BROKEN);
        assertThat(brokens).hasSize(9);
        assertThat(brokens.stream().map(Link::getLink).toList()).containsAll(List.of(
                "./folderTwo/page",
                "images/spring-boot-logo.png",
                "/doesNotExist/folder/page",
                "https://www.gogle.fr/",
                "./does-not-exist",
                "./folderTwo/page.md#does-not-exist",
                "/doesNotExist/folderOne/page",
                "/docsify/README",
                "mailto:testgmail"));

        assertThat(scanSubCommand.getScannedLinksByType(ExternalLink.class)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByType(RelativeLink.class)).hasSize(20);
        assertThat(scanSubCommand.getScannedLinksByType(MailtoLink.class)).hasSize(2);
    }

    @Test
    void shouldScanDocsifyReadme() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-c=src/test/resources/docsify", "src/test/resources/docsify/README.md");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(17);
        assertThat(scanSubCommand.getScannedLinksByStatus(SUCCESS)).hasSize(10);
        assertThat(scanSubCommand.getScannedLinksByStatus(REDIRECT)).hasSize(1);

        List<Link> brokens = scanSubCommand.getScannedLinksByStatus(BROKEN);
        assertThat(brokens).hasSize(6);
        assertThat(brokens.stream().map(Link::getLink).toList()).containsAll(List.of(
                "https://www.gogle.fr/",
                "./does-not-exist",
                "./folderTwo/page.md#does-not-exist",
                "/doesNotExist/folderOne/page",
                "/docsify/README",
                "mailto:testgmail"));

        assertThat(scanSubCommand.getScannedLinksByType(ExternalLink.class)).hasSize(3);
        assertThat(scanSubCommand.getScannedLinksByType(RelativeLink.class)).hasSize(12);
        assertThat(scanSubCommand.getScannedLinksByType(MailtoLink.class)).hasSize(2);
    }

    @Test
    void shouldScanDocsifyFolderOnePage() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-c=src/test/resources/docsify", "src/test/resources/docsify/folderOne/page.md");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(7);
        assertThat(scanSubCommand.getScannedLinksByStatus(SUCCESS)).hasSize(4);
        assertThat(scanSubCommand.getScannedLinksByStatus(REDIRECT)).isEmpty();
        List<Link> brokens = scanSubCommand.getScannedLinksByStatus(BROKEN);

        assertThat(brokens).hasSize(3);
        assertThat(brokens.stream().map(Link::getLink).toList()).containsAll(List.of(
                "./folderTwo/page",
                "images/spring-boot-logo.png",
                "/doesNotExist/folder/page"));

        assertThat(scanSubCommand.getScannedLinksByType(ExternalLink.class)).isEmpty();
        assertThat(scanSubCommand.getScannedLinksByType(RelativeLink.class)).hasSize(7);
        assertThat(scanSubCommand.getScannedLinksByType(MailtoLink.class)).isEmpty();
    }
}
