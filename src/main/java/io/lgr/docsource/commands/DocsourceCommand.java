package io.lgr.docsource.commands;

import io.lgr.docsource.utils.VersionProvider;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@Component
@CommandLine.Command(name = "docsource",
        headerHeading = "@|bold Usage|@:",
        synopsisHeading = " ",
        descriptionHeading = "%n@|bold Description|@:%n%n",
        description = "These are common Docsource commands used in various situations.",
        parameterListHeading = "%n@|bold Parameters|@:%n",
        optionListHeading = "%n@|bold Options|@:%n",
        commandListHeading = "%n@|bold Commands|@:%n",
        subcommands = { ScanSubCommand.class },
        usageHelpAutoWidth = true,
        versionProvider = VersionProvider.class,
        mixinStandardHelpOptions = true)
public class DocsourceCommand implements Callable<Integer> {
    @CommandLine.Spec
    public CommandLine.Model.CommandSpec commandSpec;

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Enable the verbose mode.", scope = CommandLine.ScopeType.INHERIT)
    public boolean verbose;

    /**
     * Execute the "docsource" command.
     * @return An execution code
     */
    @Override
    public Integer call() {
        commandSpec.commandLine().getOut().println(new CommandLine(this).getUsageMessage());
        return 0;
    }
}
