package io.github.loicgreffier.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

/**
 * This class contains unit tests for the {@link Docsource} class.
 */
class DocsourceTests {

    @Test
    void shouldDisplayUsageMessage() {
        CommandLine cmd = new CommandLine(new Docsource());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute();

        assertThat(code).isZero();
        assertThat(sw.toString()).contains("Usage:");
    }

    @Test
    void shouldEnableVerboseMode() {
        Docsource docsource = new Docsource();
        CommandLine cmd = new CommandLine(docsource);
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute("-v");

        assertThat(code).isZero();
        assertThat(docsource.verbose).isTrue();
    }
}
