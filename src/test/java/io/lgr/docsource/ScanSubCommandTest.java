package io.lgr.docsource;

import io.lgr.docsource.commands.ScanSubCommand;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScanSubCommandTest {
    @Test
    void shouldGetFile() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        List<File> files = scanSubCommand.findFiles(new File("src/test/resources/docsify/README.md"));

        assertThat(files).hasSize(1);
        assertThat(files.get(0).getName()).hasToString("README.md");
    }

    @Test
    void shouldNotGetFileIfFormatNotSupported() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        List<File> files = scanSubCommand.findFiles(new File("src/test/resources/docsify/index.html"));

        assertThat(files).isEmpty();
    }

    @Test
    void shouldGetFilesFromDirectory() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        List<File> files = scanSubCommand.findFiles(new File("src/test/resources/docsify"));

        assertThat(files).hasSize(1);
        assertThat(files.get(0).getName()).hasToString("README.md");
    }

    @Test
    void shouldGetFilesRecursivelyFromDirectory() {
        ScanSubCommand scanSubCommand = new ScanSubCommand();
        scanSubCommand.recursive = true;
        List<File> files = scanSubCommand.findFiles(new File("src/test/resources/docsify"));

        assertThat(files).hasSize(3);
        assertThat(files.stream().map(File::getName).toList()).containsAll(List.of("page.md", "README.md"));
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
