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

class CustomTest {
    @Test
    void shouldScanWholeDocumentationRecursively() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-rAc=src/test/resources/custom", "-p=content", "src/test/resources/custom");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(15);

        List<Link> successes = scanSubCommand.getScannedLinksByStatus(SUCCESS);
        assertThat(successes.stream().map(Link::getPath).toList()).containsAll(List.of(
                "https://www.google.fr/",
                "https://www.google.com",
                "./folderTwo/page",
                "folderTwo/page",
                "./folderTwo/page.md",
                "/folderTwo/page",
                "content/folderOne/images/image.jpg",
                "/content/folderOne/images/image.jpg",
                "/content/folderOne/images/image.jpg \"Image 1\"",
                "/content/folderOne/images/image.jpg 'Image 2'",
                "mailto:test@gmail"));

        List<Link> redirects = scanSubCommand.getScannedLinksByStatus(REDIRECT);
        assertThat(redirects.stream().map(Link::getPath).toList()).containsAll(List.of(
                "https://google.fr/"));

        List<Link> brokens = scanSubCommand.getScannedLinksByStatus(BROKEN);
        assertThat(brokens.stream().map(Link::getPath).toList()).containsAll(List.of(
                "https://www.gogle.fr/",
                "./folderTwo/does-not-exist",
                "mailto:testgmail"));

        List<Link> externals = scanSubCommand.getScannedLinksByType(ExternalLink.class);
        assertThat(externals.stream().map(Link::getPath).toList()).containsAll(List.of(
                "https://www.google.fr/",
                "https://www.google.com",
                "https://www.gogle.fr/",
                "https://google.fr/"));

        List<Link> relatives = scanSubCommand.getScannedLinksByType(RelativeLink.class);
        assertThat(relatives.stream().map(Link::getPath).toList()).containsAll(List.of(
                "./folderTwo/page",
                "folderTwo/page",
                "./folderTwo/page.md",
                "/folderTwo/page",
                "content/folderOne/images/image.jpg",
                "/content/folderOne/images/image.jpg",
                "/content/folderOne/images/image.jpg \"Image 1\"",
                "/content/folderOne/images/image.jpg 'Image 2'",
                "./folderTwo/does-not-exist"));

        List<Link> mailTos = scanSubCommand.getScannedLinksByType(MailtoLink.class);
        assertThat(mailTos.stream().map(Link::getPath).toList()).containsAll(List.of(
                "mailto:test@gmail",
                "mailto:testgmail"));
    }

    @Test
    void shouldScanFolderOnePage() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-Ac=src/test/resources/custom", "-p=content", "src/test/resources/custom/content/folderOne/page.md");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(15);

        List<Link> successes = scanSubCommand.getScannedLinksByStatus(SUCCESS);
        assertThat(successes.stream().map(Link::getPath).toList()).containsAll(List.of(
                "https://www.google.fr/",
                "./folderTwo/page",
                "folderTwo/page",
                "./folderTwo/page.md",
                "/folderTwo/page",
                "content/folderOne/images/image.jpg",
                "/content/folderOne/images/image.jpg",
                "/content/folderOne/images/image.jpg \"Image 1\"",
                "/content/folderOne/images/image.jpg 'Image 2'",
                "mailto:test@gmail"));

        List<Link> redirects = scanSubCommand.getScannedLinksByStatus(REDIRECT);
        assertThat(redirects.stream().map(Link::getPath).toList()).containsAll(List.of(
                "https://google.fr/"));

        List<Link> brokens = scanSubCommand.getScannedLinksByStatus(BROKEN);
        assertThat(brokens.stream().map(Link::getPath).toList()).containsAll(List.of(
                "https://www.gogle.fr/",
                "./folderTwo/does-not-exist",
                "mailto:testgmail"));

        List<Link> externals = scanSubCommand.getScannedLinksByType(ExternalLink.class);
        assertThat(externals.stream().map(Link::getPath).toList()).containsAll(List.of(
                "https://www.google.fr/",
                "https://www.gogle.fr/",
                "https://google.fr/"));

        List<Link> relatives = scanSubCommand.getScannedLinksByType(RelativeLink.class);
        assertThat(relatives.stream().map(Link::getPath).toList()).containsAll(List.of(
                "./folderTwo/page",
                "folderTwo/page",
                "./folderTwo/page.md",
                "/folderTwo/page",
                "content/folderOne/images/image.jpg",
                "/content/folderOne/images/image.jpg",
                "/content/folderOne/images/image.jpg \"Image 1\"",
                "/content/folderOne/images/image.jpg 'Image 2'",
                "./folderTwo/does-not-exist"));

        List<Link> mailTos = scanSubCommand.getScannedLinksByType(MailtoLink.class);
        assertThat(mailTos.stream().map(Link::getPath).toList()).containsAll(List.of(
                "mailto:test@gmail",
                "mailto:testgmail"));
    }
}
