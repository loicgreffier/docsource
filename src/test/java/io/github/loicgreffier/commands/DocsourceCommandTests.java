package io.github.loicgreffier.commands;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

/**
 * This class contains unit tests for the {@link DocsourceCommand} class.
 */
class DocsourceCommandTests {

    @Test
    void shouldDisplayUsageMessage() {
        CommandLine cmd = new CommandLine(new DocsourceCommand());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute();

        assertThat(code).isZero();
        assertThat(sw.toString()).contains("Usage:");
    }

    @Test
    void shouldEnableVerboseMode() {
        DocsourceCommand docsourceCommand = new DocsourceCommand();
        CommandLine cmd = new CommandLine(docsourceCommand);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute("-v");

        assertThat(code).isZero();
        assertThat(docsourceCommand.verbose).isTrue();
    }
}
