package io.lgr.docsource;

import io.lgr.docsource.commands.ScanSubCommand;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScanSubCommandTest {
    @Test
    void shouldGetFile() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        List<Path> paths = scanSubCommand.getFilesFromPath(new File("src/test/resources/docsify/README.md"));

        assertThat(paths).hasSize(1);
        assertThat(paths.get(0).getFileName().toString()).hasToString("README.md");
    }

    @Test
    void shouldNotGetFileIfFormatNotSupported() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        List<Path> paths = scanSubCommand.getFilesFromPath(new File("src/test/resources/docsify/index.html"));

        assertThat(paths).isEmpty();
    }

    @Test
    void shouldGetFilesFromFolder() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        List<Path> paths = scanSubCommand.getFilesFromPath(new File("src/test/resources/docsify"));

        assertThat(paths).hasSize(1);
        assertThat(paths.get(0).getFileName().toString()).hasToString("README.md");
    }

    @Test
    void shouldGetFilesRecursivelyFromFolder() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        scanSubCommand.recursive = true;
        List<Path> paths = scanSubCommand.getFilesFromPath(new File("src/test/resources/docsify"));

        assertThat(paths).hasSize(3);
        assertThat(paths.get(0).getFileName().toString()).hasToString("page.md");
        assertThat(paths.get(1).getFileName().toString()).hasToString("page.md");
        assertThat(paths.get(2).getFileName().toString()).hasToString("README.md");
    }
}
