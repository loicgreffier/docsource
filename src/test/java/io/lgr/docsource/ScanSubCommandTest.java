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
        assertThat(paths.stream().map(path -> path.getFileName().toString()).toList()).containsAll(List.of("page.md", "README.md"));
    }

    @Test
    void shouldGetCurrentDirParameterAsCurrentDirectory() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        scanSubCommand.currentDir = "/test";
        String currentDirectory = scanSubCommand.getCurrentDirectory();

        assertThat(currentDirectory).isEqualTo(scanSubCommand.currentDir);
    }

    @Test
    void shouldGetSystemUserDirCurrentDirectory() {
        System.setProperty("user.dir", "test");
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        String currentDirectory = scanSubCommand.getCurrentDirectory();

        assertThat(currentDirectory).isEqualTo(System.getProperty("user.dir"));
    }
}
