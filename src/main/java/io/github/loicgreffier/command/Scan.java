package io.github.loicgreffier.command;

import static io.github.loicgreffier.model.link.Link.Status.BROKEN;
import static io.github.loicgreffier.model.link.Link.Status.SUCCESS;

import io.github.loicgreffier.model.Summary;
import io.github.loicgreffier.model.link.Link;
import io.github.loicgreffier.model.link.impl.ExternalLink;
import io.github.loicgreffier.model.link.impl.MailtoLink;
import io.github.loicgreffier.model.link.impl.RelativeLink;
import io.github.loicgreffier.util.FileUtils;
import io.github.loicgreffier.util.VersionProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.Spec;

/**
 * This class represents the "docsource scan" sub command.
 */
@Component
@Command(name = "scan",
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

    @ParentCommand
    public Docsource docsource;

    @Spec
    public CommandSpec commandSpec;

    @Parameters(paramLabel = "files", description = "Directories or files to scan.")
    public List<File> inputFiles;

    @Option(names = {"-A", "--all-absolute"}, description = "Consider relative link paths as absolute paths.")
    public boolean allAbsolute;

    @Option(names = {"-k", "--insecure"}, description = "Turn off hostname and certificate chain verification.")
    public boolean insecure;

    @Option(names = {"-p",
        "--path-prefix"}, description = "Prefix the beginning of relative links with a partial path.")
    public String pathPrefix;

    @Option(names = {"-i", "--image-path-prefix"},
        description = "Prefix the beginning of images links with a partial path.")
    public String imagePathPrefix;

    @Option(names = {"-r", "--recursive"}, description = "Scan directories recursively.")
    public boolean recursive;

    @Option(names = {"--skip-external"}, description = "Skip external links.")
    public boolean skipExternal;

    @Option(names = {"--skip-relative"}, description = "Skip relative links.")
    public boolean skipRelative;

    @Option(names = {"--skip-mailto"}, description = "Skip mailto links.")
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

        displaySkipInfo();

        inputFiles.forEach(inputFile -> {
            try {
                lookUpFramework(inputFile);
            } catch (IOException e) {
                commandSpec.commandLine().getErr().println("Error while looking up the framework.");
            }

            List<File> files = findFiles(inputFile);

            files.forEach(file -> {
                Path scanFile =
                    Path.of(file.isAbsolute() ? file.getAbsolutePath() : getCurrentDirectory() + File.separator + file);

                commandSpec.commandLine().getOut().println(Help.Ansi.AUTO
                    .string("Scanning file @|bold " + scanFile + "|@"));

                try {
                    List<Link> links = FileUtils.findLinks(
                        scanFile.toFile(),
                        Link.ValidationOptions.builder()
                            .currentDir(getCurrentDirectory())
                            .pathPrefix(pathPrefix)
                            .allAbsolute(allAbsolute)
                            .skipExternal(skipExternal)
                            .skipMailto(skipMailto)
                            .skipRelative(skipRelative)
                            .insecure(insecure)
                            .build()
                    );

                    if (links.isEmpty() && docsource.verbose) {
                        commandSpec.commandLine().getOut()
                            .println(Help.Ansi.AUTO.string("No link found.\n"));
                        return;
                    }

                    links.forEach(link -> {
                        link.validate();
                        if (docsource.verbose) {
                            commandSpec.commandLine().getOut()
                                .println(Help.Ansi.AUTO.string(link.toAnsiString()));
                        }
                        scannedLinks.add(link);
                    });

                    if (docsource.verbose) {
                        commandSpec.commandLine().getOut().println();
                    }
                } catch (IOException e) {
                    commandSpec.commandLine().getErr().println("Cannot get links from file " + file + ".");
                }
            });

            if (!docsource.verbose) {
                commandSpec.commandLine().getOut().println();
            }
        });

        Summary summary = displaySummary();

        commandSpec.commandLine().getOut().println("\nEnd scanning... It's been an honour!");
        return summary.countBrokenLinks() == 0 ? 0 : 1;
    }

    /**
     * Display the skip information.
     */
    private void displaySkipInfo() {
        if (skipExternal && docsource.verbose) {
            commandSpec.commandLine().getOut().println("Skip external links requested.");
        }
        if (skipRelative && docsource.verbose) {
            commandSpec.commandLine().getOut().println("Skip relative links requested.");
        }
        if (skipMailto && docsource.verbose) {
            commandSpec.commandLine().getOut().println("Skip mailto links requested.");
        }
    }

    /**
     * Look up the framework of the given directory and apply the necessary configuration.
     *
     * @param file The file to look up.
     * @throws IOException Any IO exception during file reading.
     */
    private void lookUpFramework(File file) throws IOException {
        if (docsource.verbose) {
            commandSpec.commandLine().getOut().println("Looking up framework...");
        }

        if (FileUtils.isDocsify(file)) {
            if (docsource.verbose) {
                commandSpec.commandLine().getOut().println("Docsify framework detected.");
            }
            return; // No additional configuration needed for Docsify
        }

        if (FileUtils.isHugo(file)) {
            if (docsource.verbose) {
                commandSpec.commandLine().getOut().println("Hugo framework detected.");
            }
            imagePathPrefix = "static/";
            return;
        }

        if (docsource.verbose) {
            commandSpec.commandLine().getOut().println("No framework detected.");
        }
    }

    /**
     * Display the summary of the scan.
     *
     * @return The summary of the scan.
     */
    private Summary displaySummary() {
        commandSpec.commandLine().getOut()
            .println(Help.Ansi.AUTO.string("@|bold Summary |@"));
        commandSpec.commandLine().getOut().println(
            Help.Ansi.AUTO.string(new String(new char[40]).replace("\0", "-")));

        Help.TextTable textTable = Help.TextTable.forColumns(
            Help.defaultColorScheme(Help.Ansi.AUTO),
            new Help.Column(7, 0, Help.Column.Overflow.SPAN),
            new Help.Column(10, 2, Help.Column.Overflow.SPAN),
            new Help.Column(10, 2, Help.Column.Overflow.SPAN),
            new Help.Column(skipMailto ? 9 : 6, 2,
                Help.Column.Overflow.SPAN),
            new Help.Column(7, 2, Help.Column.Overflow.SPAN));

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
                .println(Help.Ansi.AUTO.string("@|bold Broken links |@"));
        } else {
            commandSpec.commandLine().getOut()
                .println(Help.Ansi.AUTO.string("@|bold No broken links. |@"));
        }

        getScannedLinksByStatus(BROKEN)
            .stream()
            .collect(Collectors.groupingBy(link -> link.getFile().getAbsolutePath()))
            .forEach((key, value) -> {
                commandSpec.commandLine().getOut().println(key);
                value.forEach(brokenLink -> commandSpec.commandLine().getOut().println(
                    Help.Ansi.AUTO.string("  - " + brokenLink.toAnsiString())));
            });

        return new Summary(
            successRelative,
            successExternal,
            successMail,
            brokenRelative,
            brokenExternal,
            brokenMail
        );
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

        Path scanDirectory =
            Path.of(file.isAbsolute() ? file.getAbsolutePath() : getCurrentDirectory() + File.separator + file);

        commandSpec.commandLine().getOut().println(Help.Ansi.AUTO.string(
            "Scanning directory @|bold " + scanDirectory + "|@"));

        try {
            List<File> files = FileUtils.findFiles(scanDirectory.toFile(), recursive);
            commandSpec.commandLine().getOut().println(Help.Ansi.AUTO.string(
                "Found @|bold " + files.size() + " file(s)|@ to scan"));
            return files;
        } catch (IOException e) {
            commandSpec.commandLine().getErr()
                .println("Cannot retrieve files from directory " + scanDirectory);
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
        return System.getProperty("user.dir");
    }
}
