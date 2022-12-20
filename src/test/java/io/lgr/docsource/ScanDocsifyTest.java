package io.lgr.docsource;

import io.lgr.docsource.commands.ScanSubCommand;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.util.stream.Collectors;

import static io.lgr.docsource.models.Link.Status.SUCCESS;
import static io.lgr.docsource.models.Link.Type.*;
import static io.lgr.docsource.models.Link.Type.LOCAL;
import static org.assertj.core.api.Assertions.assertThat;

class ScanDocsifyTest {

    @Test
    void shouldScanWholeDocsifyFolder() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-r", "src/test/resources/docsify");

        assertThat(code).isZero();

        assertThat(scanSubCommand.getScannedFiles()).hasSize(2);
        assertThat(scanSubCommand.getScannedFiles().get(0).getLinks()).hasSize(1);
        assertThat(scanSubCommand.getScannedFiles().get(0).getLinks()
                .stream()
                .filter(link -> SUCCESS.equals(link.getStatus()))
                .collect(Collectors.toList())).hasSize(1);

        assertThat(scanSubCommand.getScannedFiles().get(1).getLinks()).hasSize(6);
        assertThat(scanSubCommand.getScannedFiles().get(1).getLinks()
                .stream()
                .filter(link -> SUCCESS.equals(link.getStatus()))
                .collect(Collectors.toList())).hasSize(6);
        assertThat(scanSubCommand.getScannedFiles().get(1).getLinks()
                .stream()
                .filter(link -> REMOTE.equals(link.getType()))
                .collect(Collectors.toList())).hasSize(1);
        assertThat(scanSubCommand.getScannedFiles().get(1).getLinks()
                .stream()
                .filter(link -> EMAIL.equals(link.getType()))
                .collect(Collectors.toList())).hasSize(1);
        assertThat(scanSubCommand.getScannedFiles().get(1).getLinks()
                .stream()
                .filter(link -> LOCAL.equals(link.getType()))
                .collect(Collectors.toList())).hasSize(4);
    }

    @Test
    void shouldScanDocsifyReadme() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-r", "src/test/resources/docsify/README.md");

        assertThat(code).isZero();

        assertThat(scanSubCommand.getScannedFiles()).hasSize(1);
        assertThat(scanSubCommand.getScannedFiles().get(0).getLinks()
                .stream()
                .filter(link -> SUCCESS.equals(link.getStatus()))
                .collect(Collectors.toList())).hasSize(6);
        assertThat(scanSubCommand.getScannedFiles().get(0).getLinks()
                .stream()
                .filter(link -> REMOTE.equals(link.getType()))
                .collect(Collectors.toList())).hasSize(1);
        assertThat(scanSubCommand.getScannedFiles().get(0).getLinks()
                .stream()
                .filter(link -> EMAIL.equals(link.getType()))
                .collect(Collectors.toList())).hasSize(1);
        assertThat(scanSubCommand.getScannedFiles().get(0).getLinks()
                .stream()
                .filter(link -> LOCAL.equals(link.getType()))
                .collect(Collectors.toList())).hasSize(4);
    }

    @Test
    void shouldScanDocsifyPage() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        int code = new CommandLine(scanSubCommand).execute("-r", "src/test/resources/docsify/folder/page.md");

        assertThat(code).isZero();

        assertThat(scanSubCommand.getScannedFiles()).hasSize(1);
        assertThat(scanSubCommand.getScannedFiles().get(0).getLinks()
                .stream()
                .filter(link -> SUCCESS.equals(link.getStatus()))
                .collect(Collectors.toList())).hasSize(1);
    }
}
