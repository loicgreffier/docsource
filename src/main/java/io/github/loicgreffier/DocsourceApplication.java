package io.github.loicgreffier;

import io.github.loicgreffier.commands.DocsourceCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import picocli.jansi.graalvm.AnsiConsole;

/**
 * This is the main class for the Docsource application.
 */
@SpringBootApplication
public class DocsourceApplication implements CommandLineRunner {
    @Autowired
    private CommandLine.IFactory factory;

    @Autowired
    private DocsourceCommand docsourceCommand;

    /**
     * The main entry point of the Docsource application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(DocsourceApplication.class, args);
    }

    /**
     * Run the Docsource command line.
     *
     * @param args The command line arguments.
     */
    @Override
    public void run(String... args) {
        int exitCode;
        try (
            // Colors on Windows CMD (including for native)
            AnsiConsole ansi = AnsiConsole.windowsInstall()) {
            exitCode = new CommandLine(docsourceCommand, factory).execute(args);
        }
        System.exit(exitCode);
    }
}
