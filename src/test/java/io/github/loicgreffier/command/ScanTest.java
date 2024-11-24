package io.github.loicgreffier.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class ScanTest {
    @Test
    void shouldDisplayUsageMessage() {
        CommandLine cmd = new CommandLine(new Scan());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int code = cmd.execute();

        assertThat(code).isZero();
        assertThat(sw.toString()).contains("Usage:");
    }
}
