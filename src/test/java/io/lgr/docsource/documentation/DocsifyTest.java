package io.lgr.docsource.documentation;

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

class DocsifyTest {
    @Test
    void shouldScanWholeDocumentationRecursively() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-rc=src/test/resources/docsify", "src/test/resources/docsify");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(20);

        List<Link> successes = scanSubCommand.getScannedLinksByStatus(SUCCESS);
        assertThat(successes).hasSize(11);
        assertThat(successes.stream().map(Link::getLink).toList()).containsAll(List.of(
                "https://www.google.fr/",
                "./folderOne/page",
                "folderOne/page",
                "./folderOne/page.md",
                "/folderOne/page",
                "images/spring-boot-logo.png",
                "mailto:test@gmail",
                "../README",
                "../folderTwo/page",
                "/README",
                "/"));

        List<Link> redirects = scanSubCommand.getScannedLinksByStatus(REDIRECT);
        assertThat(redirects).hasSize(1);
        assertThat(redirects.stream().map(Link::getLink).toList()).containsAll(List.of(
                "https://google.fr/"));

        List<Link> brokens = scanSubCommand.getScannedLinksByStatus(BROKEN);
        assertThat(brokens).hasSize(8);
        assertThat(brokens.stream().map(Link::getLink).toList()).containsAll(List.of(
                "./folderTwo/page",
                "images/spring-boot-logo.png",
                "/doesNotExist/folder/page",
                "https://www.gogle.fr/",
                "./does-not-exist",
                "/doesNotExist/folderOne/page",
                "/docsify/README",
                "mailto:testgmail"));

        List<Link> externals = scanSubCommand.getScannedLinksByType(ExternalLink.class);
        assertThat(externals).hasSize(3);
        assertThat(externals.stream().map(Link::getLink).toList()).containsAll(List.of(
                "https://www.google.fr/",
                "https://www.gogle.fr/",
                "https://google.fr/"));

        List<Link> relatives = scanSubCommand.getScannedLinksByType(RelativeLink.class);
        assertThat(relatives).hasSize(15);
        assertThat(relatives.stream().map(Link::getLink).toList()).containsAll(List.of(
                "./folderOne/page",
                "folderOne/page",
                "./folderOne/page.md",
                "/folderOne/page",
                "images/spring-boot-logo.png",
                "./does-not-exist",
                "/doesNotExist/folderOne/page",
                "/docsify/README",
                "../README",
                "../folderTwo/page",
                "/README",
                "/",
                "./folderTwo/page",
                "images/spring-boot-logo.png",
                "/doesNotExist/folder/page"));

        List<Link> mailTos = scanSubCommand.getScannedLinksByType(MailtoLink.class);
        assertThat(mailTos).hasSize(2);
        assertThat(mailTos.stream().map(Link::getLink).toList()).containsAll(List.of(
                "mailto:test@gmail",
                "mailto:testgmail"));
    }

    @Test
    void shouldScanReadme() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-c=src/test/resources/docsify", "src/test/resources/docsify/README.md");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(13);

        List<Link> successes = scanSubCommand.getScannedLinksByStatus(SUCCESS);
        assertThat(successes).hasSize(7);
        assertThat(successes.stream().map(Link::getLink).toList()).containsAll(List.of(
                "https://www.google.fr/",
                "./folderOne/page",
                "folderOne/page",
                "./folderOne/page.md",
                "/folderOne/page",
                "images/spring-boot-logo.png",
                "mailto:test@gmail"));

        List<Link> redirects = scanSubCommand.getScannedLinksByStatus(REDIRECT);
        assertThat(redirects).hasSize(1);
        assertThat(redirects.stream().map(Link::getLink).toList()).containsAll(List.of(
                "https://google.fr/"));

        List<Link> brokens = scanSubCommand.getScannedLinksByStatus(BROKEN);
        assertThat(brokens).hasSize(5);
        assertThat(brokens.stream().map(Link::getLink).toList()).containsAll(List.of(
                "https://www.gogle.fr/",
                "./does-not-exist",
                "/doesNotExist/folderOne/page",
                "/docsify/README",
                "mailto:testgmail"));

        List<Link> externals = scanSubCommand.getScannedLinksByType(ExternalLink.class);
        assertThat(externals).hasSize(3);
        assertThat(externals.stream().map(Link::getLink).toList()).containsAll(List.of(
                "https://www.google.fr/",
                "https://www.gogle.fr/",
                "https://google.fr/"));

        List<Link> relatives = scanSubCommand.getScannedLinksByType(RelativeLink.class);
        assertThat(relatives).hasSize(8);
        assertThat(relatives.stream().map(Link::getLink).toList()).containsAll(List.of(
                "./folderOne/page",
                "folderOne/page",
                "./folderOne/page.md",
                "/folderOne/page",
                "images/spring-boot-logo.png",
                "./does-not-exist",
                "/doesNotExist/folderOne/page",
                "/docsify/README"));

        List<Link> mailTos = scanSubCommand.getScannedLinksByType(MailtoLink.class);
        assertThat(mailTos).hasSize(2);
        assertThat(mailTos.stream().map(Link::getLink).toList()).containsAll(List.of(
                "mailto:test@gmail",
                "mailto:testgmail"));
    }

    @Test
    void shouldScanFolderOnePage() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-c=src/test/resources/docsify", "src/test/resources/docsify/folderOne/page.md");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(7);

        List<Link> successes = scanSubCommand.getScannedLinksByStatus(SUCCESS);
        assertThat(successes).hasSize(4);
        assertThat(successes.stream().map(Link::getLink).toList()).containsAll(List.of(
                "../README",
                "../folderTwo/page",
                "/README",
                "/"));

        List<Link> redirects = scanSubCommand.getScannedLinksByStatus(REDIRECT);
        assertThat(redirects).isEmpty();

        List<Link> brokens = scanSubCommand.getScannedLinksByStatus(BROKEN);
        assertThat(brokens).hasSize(3);
        assertThat(brokens.stream().map(Link::getLink).toList()).containsAll(List.of(
                "./folderTwo/page",
                "images/spring-boot-logo.png",
                "/doesNotExist/folder/page"));

        List<Link> externals = scanSubCommand.getScannedLinksByType(ExternalLink.class);
        assertThat(externals).isEmpty();

        List<Link> relatives = scanSubCommand.getScannedLinksByType(RelativeLink.class);
        assertThat(relatives).hasSize(7);
        assertThat(relatives.stream().map(Link::getLink).toList()).containsAll(List.of(
                "../README",
                "../folderTwo/page",
                "/README",
                "/",
                "./folderTwo/page",
                "images/spring-boot-logo.png",
                "/doesNotExist/folder/page"));

        List<Link> mailTos = scanSubCommand.getScannedLinksByType(MailtoLink.class);
        assertThat(mailTos).isEmpty();
    }
}
