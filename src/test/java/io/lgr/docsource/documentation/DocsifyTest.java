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
        assertThat(scanSubCommand.getScannedLinks()).hasSize(24);

        List<Link> successes = scanSubCommand.getScannedLinksByStatus(SUCCESS);
        assertThat(successes.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
                "https://www.google.fr/",
                "https://www.google.com",
                "./folderOne/page",
                "folderOne/page",
                "./folderOne/page.md",
                "/folderOne/page",
                "images/image.jpg",
                "images/image.jpg \"Image 1\"",
                "images/image.jpg 'Image 2'",
                "images/image%20with%20spaces.jpg \"Image with spaces\"",
                "mailto:test@gmail",
                "../README",
                "../folderTwo/page",
                "/README",
                "/"));

        List<Link> redirects = scanSubCommand.getScannedLinksByStatus(REDIRECT);
        assertThat(redirects.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
                "https://google.fr/"));

        List<Link> brokens = scanSubCommand.getScannedLinksByStatus(BROKEN);
        assertThat(brokens.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
                "./folderTwo/page",
                "images/image.jpg",
                "/doesNotExist/folder/page",
                "https://www.gogle.fr/",
                "./does-not-exist",
                "/doesNotExist/folderOne/page",
                "/docsify/README",
                "mailto:testgmail"));

        List<Link> externals = scanSubCommand.getScannedLinksByType(ExternalLink.class);
        assertThat(externals.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
                "https://www.google.fr/",
                "https://www.google.com",
                "https://www.gogle.fr/",
                "https://google.fr/"));

        List<Link> relatives = scanSubCommand.getScannedLinksByType(RelativeLink.class);
        assertThat(relatives.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
                "./folderOne/page",
                "folderOne/page",
                "./folderOne/page.md",
                "/folderOne/page",
                "images/image.jpg",
                "images/image.jpg \"Image 1\"",
                "images/image.jpg 'Image 2'",
                "images/image%20with%20spaces.jpg \"Image with spaces\"",
                "./does-not-exist",
                "/doesNotExist/folderOne/page",
                "/docsify/README",
                "../README",
                "../folderTwo/page",
                "/README",
                "/",
                "./folderTwo/page",
                "images/image.jpg",
                "/doesNotExist/folder/page"));

        List<Link> mailTos = scanSubCommand.getScannedLinksByType(MailtoLink.class);
        assertThat(mailTos.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
                "mailto:test@gmail",
                "mailto:testgmail"));
    }

    @Test
    void shouldScanWholeDocumentationSkippingAllLinks() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-rc=src/test/resources/docsify",
                "--skip-external",
                "--skip-relative",
                "--skip-mailto",
                "src/test/resources/docsify");

        assertThat(code).isZero();
        assertThat(scanSubCommand.getScannedLinks()).isEmpty();
    }

    @Test
    void shouldScanWholeDocumentationTrustingAllCertificates() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-rc=src/test/resources/docsify",
                "--trust-all-certificates",
                "src/test/resources/docsify");

        assertThat(code).isNotZero();
        assertThat(System.getProperty("jdk.internal.httpclient.disableHostnameVerification")).isEqualTo(Boolean.TRUE.toString());
    }

    @Test
    void shouldScanReadme() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-c=src/test/resources/docsify", "src/test/resources/docsify/README.md");

        assertThat(code).isNotZero();
        assertThat(scanSubCommand.getScannedLinks()).hasSize(17);

        List<Link> successes = scanSubCommand.getScannedLinksByStatus(SUCCESS);
        assertThat(successes.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
                "https://www.google.fr/",
                "https://www.google.com",
                "./folderOne/page",
                "folderOne/page",
                "./folderOne/page.md",
                "/folderOne/page",
                "images/image.jpg",
                "images/image.jpg \"Image 1\"",
                "images/image.jpg 'Image 2'",
                "images/image%20with%20spaces.jpg \"Image with spaces\"",
                "mailto:test@gmail"));

        List<Link> redirects = scanSubCommand.getScannedLinksByStatus(REDIRECT);
        assertThat(redirects.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
                "https://google.fr/"));

        List<Link> brokens = scanSubCommand.getScannedLinksByStatus(BROKEN);
        assertThat(brokens.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
                "https://www.gogle.fr/",
                "./does-not-exist",
                "/doesNotExist/folderOne/page",
                "/docsify/README",
                "mailto:testgmail"));

        List<Link> externals = scanSubCommand.getScannedLinksByType(ExternalLink.class);
        assertThat(externals.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
                "https://www.google.fr/",
                "https://www.google.com",
                "https://www.gogle.fr/",
                "https://google.fr/"));

        List<Link> relatives = scanSubCommand.getScannedLinksByType(RelativeLink.class);
        assertThat(relatives.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
                "./folderOne/page",
                "folderOne/page",
                "./folderOne/page.md",
                "/folderOne/page",
                "images/image.jpg",
                "images/image.jpg \"Image 1\"",
                "images/image.jpg 'Image 2'",
                "images/image%20with%20spaces.jpg \"Image with spaces\"",
                "./does-not-exist",
                "/doesNotExist/folderOne/page",
                "/docsify/README"));

        List<Link> mailTos = scanSubCommand.getScannedLinksByType(MailtoLink.class);
        assertThat(mailTos.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
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
        assertThat(successes.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
                "../README",
                "../folderTwo/page",
                "/README",
                "/"));

        List<Link> redirects = scanSubCommand.getScannedLinksByStatus(REDIRECT);
        assertThat(redirects).isEmpty();

        List<Link> brokens = scanSubCommand.getScannedLinksByStatus(BROKEN);
        assertThat(brokens.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
                "./folderTwo/page",
                "images/image.jpg",
                "/doesNotExist/folder/page"));

        List<Link> externals = scanSubCommand.getScannedLinksByType(ExternalLink.class);
        assertThat(externals).isEmpty();

        List<Link> relatives = scanSubCommand.getScannedLinksByType(RelativeLink.class);
        assertThat(relatives.stream().map(Link::getPath).toList()).containsExactlyInAnyOrderElementsOf(List.of(
                "../README",
                "../folderTwo/page",
                "/README",
                "/",
                "./folderTwo/page",
                "images/image.jpg",
                "/doesNotExist/folder/page"));

        List<Link> mailTos = scanSubCommand.getScannedLinksByType(MailtoLink.class);
        assertThat(mailTos).isEmpty();
    }
}
