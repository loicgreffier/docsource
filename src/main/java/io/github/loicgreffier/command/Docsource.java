package io.github.loicgreffier.command;

import io.github.loicgreffier.util.VersionProvider;
import java.util.concurrent.Callable;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ScopeType;
import picocli.CommandLine.Spec;

/**
 * This class represents the "docsource" command.
 */
@Component
@Command(name = "docsource",
    headerHeading = "@|bold Usage|@:",
    synopsisHeading = " ",
    descriptionHeading = "%n@|bold Description|@:%n%n",
    description = "These are common Docsource commands used in various situations.",
    parameterListHeading = "%n@|bold Parameters|@:%n",
    optionListHeading = "%n@|bold Options|@:%n",
    commandListHeading = "%n@|bold Commands|@:%n",
    subcommands = {Scan.class},
    usageHelpAutoWidth = true,
    versionProvider = VersionProvider.class,
    mixinStandardHelpOptions = true)
public class Docsource implements Callable<Integer> {
    @Spec
    public CommandSpec commandSpec;

    @Option(names = {"-v", "--verbose"},
        description = "Enable the verbose mode.", scope = ScopeType.INHERIT)
    public boolean verbose;

    /**
     * Run the "docsource" command.
     * When the command is run without any subcommand, the usage message is printed.
     *
     * @return The exit code.
     */
    @Override
    public Integer call() {
        commandSpec.commandLine().getOut().println(new CommandLine(this).getUsageMessage());
        return 0;
    }
}
