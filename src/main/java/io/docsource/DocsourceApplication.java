package io.docsource;

import io.docsource.commands.DocsourceCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import picocli.jansi.graalvm.AnsiConsole;

@SpringBootApplication
public class DocsourceApplication implements CommandLineRunner {
    @Autowired
    private CommandLine.IFactory factory;

    @Autowired
    private DocsourceCommand docsourceCommand;

    /**
     * Main method
     * @param args The program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(DocsourceApplication.class, args);
    }

    /**
     * Run method
     * @param args The program arguments
     */
    @Override
    public void run(String... args) {
        int exitCode;
        try (AnsiConsole ansi = AnsiConsole.windowsInstall()) { // Colors on Windows CMD (including for native)
            exitCode = new CommandLine(docsourceCommand, factory).execute(args);
        }
        System.exit(exitCode);
    }
}
