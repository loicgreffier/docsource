package io.github.loicgreffier.command;

import static io.github.loicgreffier.model.Link.Status.BROKEN;
import static io.github.loicgreffier.model.Link.Status.SUCCESS;

import io.github.loicgreffier.model.Link;
import io.github.loicgreffier.model.impl.ExternalLink;
import io.github.loicgreffier.model.impl.MailtoLink;
import io.github.loicgreffier.model.impl.RelativeLink;
import io.github.loicgreffier.util.FileUtils;
import io.github.loicgreffier.util.VersionProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

/**
 * This class represents the "docsource scan" sub command.
 */
@Component
@CommandLine.Command(name = "scan",
    headerHeading = "@|bold Usage|@:",
    synopsisHeading = " ",
    descriptionHeading = "%n@|bold Description|@:%n%n",
    description = "Scan documentation.",
    parameterListHeading = "%n@|bold Parameters|@:%n",
    optionListHeading = "%n@|bold Options|@:%n",
    commandListHeading = "%n@|bold Commands|@:%n",
    usageHelpAutoWidth = true,
    versionProvider = VersionProvider.class,
    mixinStandardHelpOptions = true)
public class Scan implements Callable<Integer> {
    private static final String SKIPPED = "Skipped";

    @CommandLine.ParentCommand
    public Docsource docsource;

    @CommandLine.Spec
    public CommandLine.Model.CommandSpec commandSpec;

    @CommandLine.Parameters(paramLabel = "files", description = "Directories or files to scan.")
    public List<File> inputFiles;

    @CommandLine.Option(names = {"-A",
        "--all-absolute"}, description = "Consider relative link paths as absolute paths.")
    public boolean allAbsolute;

    @CommandLine.Option(names = {"-c",
        "--current-dir"}, description = "Override the current directory.")
    public String currentDir;

    @CommandLine.Option(names = {"-k",
        "--insecure"}, description = "Turn off hostname and certificate chain verification.")
    public boolean insecure;

    @CommandLine.Option(names = {"-p", "--path-prefix"},
        description = "Prefix the beginning of relative links with a partial path.")
    public String pathPrefix;

    @CommandLine.Option(names = {"-r",
        "--recursive"}, description = "Scan directories recursively.")
    public boolean recursive;

    @CommandLine.Option(names = {"--skip-external"}, description = "Skip external links.")
    public boolean skipExternal;

    @CommandLine.Option(names = {"--skip-relative"}, description = "Skip relative links.")
    public boolean skipRelative;

    @CommandLine.Option(names = {"--skip-mailto"}, description = "Skip mailto links.")
    public boolean skipMailto;

    private final List<Link> scannedLinks = new ArrayList<>();

    /**
     * Run the "docsource scan" sub command.
     * When the command is run without any input file, the usage message is printed.
     * When the command is run with input files, the links are scanned.
     *
     * @return The exit code.
     */
    @Override
    public Integer call() {
        if (inputFiles == null) {
            commandSpec.commandLine().getOut().println(new CommandLine(this).getUsageMessage());
            return 0;
        }

        if (skipExternal && docsource.verbose) {
            commandSpec.commandLine().getOut().println("Skip external links requested.");
        }
        if (skipRelative && docsource.verbose) {
            commandSpec.commandLine().getOut().println("Skip relative links requested.");
        }
        if (skipMailto && docsource.verbose) {
            commandSpec.commandLine().getOut().println("Skip mailto links requested.");
        }

        inputFiles.forEach(inputFile -> {
            List<File> files = findFiles(inputFile);

            files.forEach(file -> {
                commandSpec.commandLine().getOut().println(CommandLine.Help.Ansi.AUTO
                    .string("Scanning file @|bold " + file.getAbsolutePath() + "|@"));

                try {
                    List<Link> links = FileUtils.findLinks(file, Link.ValidationOptions.builder()
                        .currentDir(getCurrentDirectory()).pathPrefix(pathPrefix)
                        .allAbsolute(allAbsolute)
                        .skipExternal(skipExternal).skipMailto(skipMailto)
                        .skipRelative(skipRelative)
                        .insecure(insecure).build());
                    if (links.isEmpty() && docsource.verbose) {
                        commandSpec.commandLine().getOut()
                            .println(CommandLine.Help.Ansi.AUTO.string("No link found.\n"));
                        return;
                    }

                    links.forEach(link -> {
                        link.validate();
                        if (docsource.verbose) {
                            commandSpec.commandLine().getOut()
                                .println(CommandLine.Help.Ansi.AUTO.string(link.toAnsiString()));
                        }
                        scannedLinks.add(link);
                    });

                    if (docsource.verbose) {
                        commandSpec.commandLine().getOut().println();
                    }
                } catch (IOException e) {
                    commandSpec.commandLine().getErr()
                        .println("Cannot get links from file " + file + ".");
                }
            });
            if (!docsource.verbose) {
                commandSpec.commandLine().getOut().println();
            }
        });

        // Display summary
        commandSpec.commandLine().getOut()
            .println(CommandLine.Help.Ansi.AUTO.string("@|bold Summary |@"));
        commandSpec.commandLine().getOut().println(
            CommandLine.Help.Ansi.AUTO.string(new String(new char[40]).replace("\0", "-")));

        CommandLine.Help.TextTable textTable = CommandLine.Help.TextTable.forColumns(
            CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.AUTO),
            new CommandLine.Help.Column(7, 0, CommandLine.Help.Column.Overflow.SPAN),
            new CommandLine.Help.Column(10, 2, CommandLine.Help.Column.Overflow.SPAN),
            new CommandLine.Help.Column(10, 2, CommandLine.Help.Column.Overflow.SPAN),
            new CommandLine.Help.Column(skipMailto ? 9 : 6, 2,
                CommandLine.Help.Column.Overflow.SPAN),
            new CommandLine.Help.Column(7, 2, CommandLine.Help.Column.Overflow.SPAN));

        long successRelative = countScannedLinksByTypeAndStatus(RelativeLink.class, SUCCESS);
        long successExternal = countScannedLinksByTypeAndStatus(ExternalLink.class, SUCCESS);
        long successMail = countScannedLinksByTypeAndStatus(MailtoLink.class, SUCCESS);
        long brokenRelative = countScannedLinksByTypeAndStatus(RelativeLink.class, BROKEN);
        long brokenExternal = countScannedLinksByTypeAndStatus(ExternalLink.class, BROKEN);
        long brokenMail = countScannedLinksByTypeAndStatus(MailtoLink.class, BROKEN);

        String displaySuccessRelative = skipRelative ? SKIPPED : String.valueOf(successRelative);
        String displaySuccessExternal = skipExternal ? SKIPPED : String.valueOf(successExternal);
        String displaySuccessMail = skipMailto ? SKIPPED : String.valueOf(successMail);
        String displayBrokenRelative = skipRelative ? SKIPPED : String.valueOf(brokenRelative);
        String displayBrokenExternal = skipExternal ? SKIPPED : String.valueOf(brokenExternal);
        String displayBrokenMail = skipMailto ? SKIPPED : String.valueOf(brokenMail);

        textTable.addRowValues("", "Relative", "External", "Mail", "Total");
        textTable.addRowValues("Success", displaySuccessRelative, displaySuccessExternal,
            displaySuccessMail,
            String.valueOf(successRelative + successExternal + successMail));
        textTable.addRowValues("Broken", displayBrokenRelative, displayBrokenExternal,
            displayBrokenMail,
            String.valueOf(brokenRelative + brokenExternal + brokenMail));
        textTable.addRowValues("Total", String.valueOf(successRelative + brokenRelative),
            String.valueOf(successExternal + brokenExternal),
            String.valueOf(successMail + brokenMail),
            String.valueOf(
                successRelative + successExternal + successMail + brokenRelative + brokenExternal
                    + brokenMail));

        commandSpec.commandLine().getOut().println(textTable);

        if (brokenRelative + brokenExternal + brokenMail > 0) {
            commandSpec.commandLine().getOut()
                .println(CommandLine.Help.Ansi.AUTO.string("@|bold Broken links |@"));
        } else {
            commandSpec.commandLine().getOut()
                .println(CommandLine.Help.Ansi.AUTO.string("@|bold No broken links. |@"));
        }

        getScannedLinksByStatus(BROKEN)
            .stream()
            .collect(Collectors.groupingBy(link -> link.getFile().getAbsolutePath()))
            .forEach((key, value) -> {
                commandSpec.commandLine().getOut().println(key);
                value.forEach(brokenLink -> commandSpec.commandLine().getOut().println(
                    CommandLine.Help.Ansi.AUTO.string("  - " + brokenLink.toAnsiString())));
            });

        commandSpec.commandLine().getOut().println("\nEnd scanning... It's been an honour!");
        return brokenRelative + brokenExternal + brokenMail == 0 ? 0 : 1;
    }

    /**
     * Find files to scan from a file or a directory.
     *
     * @param file The file to scan.
     * @return A list of files to scan.
     */
    public List<File> findFiles(File file) {
        if (file.isFile()) {
            if (!FileUtils.isAuthorized(file)) {
                commandSpec.commandLine().getErr()
                    .println("The format of the " + file + " file is not supported.");
                return List.of();
            }
            return List.of(file);
        }

        commandSpec.commandLine().getOut().println(CommandLine.Help.Ansi.AUTO.string(
            "Scanning directory @|bold " + file.getAbsolutePath() + "|@"));

        try {
            List<File> files = FileUtils.findFiles(file, recursive);
            commandSpec.commandLine().getOut().println(CommandLine.Help.Ansi.AUTO.string(
                "Found @|bold " + files.size() + " file(s)|@ to scan"));
            return files;
        } catch (IOException e) {
            commandSpec.commandLine().getErr()
                .println("Cannot retrieve files from directory " + file.getAbsolutePath());
        }

        return List.of();
    }

    /**
     * Count the scanned links by type and status.
     *
     * @param linkClass The link class to count.
     * @param status    The link status to count.
     * @param <T>       The link type.
     * @return The number of scanned links by type and status.
     */
    public <T extends Link> long countScannedLinksByTypeAndStatus(Class<T> linkClass,
                                                                  Link.Status status) {
        return scannedLinks
            .stream()
            .filter(link -> linkClass.isInstance(link) && status.equals(link.getStatus()))
            .count();
    }

    /**
     * Get the scanned links by status.
     *
     * @param status The link status to get.
     * @return The scanned links by status.
     */
    public List<Link> getScannedLinksByStatus(Link.Status status) {
        return scannedLinks
            .stream()
            .filter(link -> status.equals(link.getStatus()))
            .toList();
    }

    /**
     * Get the current directory.
     *
     * @return The current directory.
     */
    public String getCurrentDirectory() {
        if (currentDir != null) {
            return currentDir;
        }

        return System.getProperty("user.dir");
    }
}
