package io.lgr.docsource.commands;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import io.lgr.docsource.utils.VersionProvider;
import lombok.Getter;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.List;
import java.util.Map;
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
    /**
     * If "-v" is given, set verbose mode.
     * @param verbose The verbose mode
     */
    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Enable the verbose mode.", scope = CommandLine.ScopeType.INHERIT)
    public void setVerbose(final boolean verbose) {
        if (verbose) {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            loggerContext.exists("io.lgr.docsource").setLevel(Level.DEBUG);
        }
    }

    /**
     * Execute the "docsource" command.
     * @return An execution code
     */
    @Override
    public Integer call() {
        CommandLine docsource = new CommandLine(this);
        docsource.usage(System.out);
        return 0;
    }
}
