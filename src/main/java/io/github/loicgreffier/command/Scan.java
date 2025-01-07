/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.github.loicgreffier.command;

import static io.github.loicgreffier.model.link.Link.Status.BROKEN;
import static io.github.loicgreffier.model.link.Link.Status.SUCCESS;
import static io.github.loicgreffier.util.RegexUtils.hugoRegex;
import static io.github.loicgreffier.util.RegexUtils.markdownRegex;

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
import java.util.Optional;
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

    @Parameters(paramLabel = "files", description = "Root directories or files to scan.")
    public List<File> inputFiles;

    @Option(names = {"-A",
        "--all-absolute"}, description = "Consider relative paths as absolute paths.")
    public boolean allAbsolute;

    @Option(
        names = {"--content-directory"},
        description = "Specify a sub-directory of the root directory "
            + "containing the Markdown files. E.g., 'content' for Hugo."
    )
    public String contentDirectory;

    @Option(
        names = {"-I", "--image-absolute"},
        description = "Consider relative image paths as absolute paths."
    )
    public Boolean imageAbsolute;

    @Option(
        names = {"--image-directory"},
        description = "Specify a sub-directory of the root directory containing the images. E.g., 'static' for Hugo."
    )
    public String imageDirectory;

    @Option(
        names = {"--index-filename"},
        description = "Specify the filename to use as an index file. E.g., '_index.md' for Hugo."
    )
    public String indexFilename;

    @Option(
        names = {"-k", "--insecure"},
        description = "Turn off hostname and certificate chain verification."
    )
    public boolean insecure;

    @Option(names = {"-r", "--recursive"}, description = "Scan directories recursively.")
    public boolean recursive;

    @Option(names = {"--skip-external"}, description = "Skip external links.")
    public boolean skipExternal;

    @Option(names = {"--skip-mailto"}, description = "Skip mailto links.")
    public boolean skipMailto;

    @Option(names = {"--skip-relative"}, description = "Skip relative links.")
    public boolean skipRelative;

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
                List<String> regexs = lookUpFramework(getCurrentDirectory());
                List<File> files = findFiles(inputFile);

                files.forEach(file -> {
                    commandSpec.commandLine().getOut().println(Help.Ansi.AUTO
                        .string("Scanning file @|bold " + file + "|@"));

                    try {
                        findAndValidateLinks(file, regexs);

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
            } catch (IOException e) {
                commandSpec.commandLine().getErr().println("Error while looking up the framework.");
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
     * Look up the framework of the given directory, apply the necessary configuration and return the regex to use.
     *
     * @param file The file to look up.
     * @return The regex to use.
     * @throws IOException Any IO exception during file reading.
     */
    private List<String> lookUpFramework(String file) throws IOException {
        if (docsource.verbose) {
            commandSpec.commandLine().getOut().println("Looking up framework...");
        }

        if (FileUtils.isDocsify(file)) {
            if (docsource.verbose) {
                commandSpec.commandLine().getOut().println("Docsify framework detected.\n");
            }
            return markdownRegex(); // No additional configuration needed for Docsify
        }

        if (FileUtils.isHugo(file)) {
            if (docsource.verbose) {
                commandSpec.commandLine().getOut().println("Hugo framework detected.\n");
            }
            contentDirectory = Optional.ofNullable(contentDirectory).orElse("content");
            imageDirectory = Optional.ofNullable(imageDirectory).orElse("static");
            indexFilename = Optional.ofNullable(indexFilename).orElse("_index.md");
            imageAbsolute = Optional.ofNullable(imageAbsolute).orElse(true);
            return hugoRegex();
        }

        if (docsource.verbose) {
            commandSpec.commandLine().getOut().println("No framework detected.\n");
        }
        return markdownRegex();
    }

    /**
     * Find and validate links in a file.
     *
     * @param file   The file to scan.
     * @param regexs The regex to look for.
     * @throws IOException Any IO exception during file reading.
     */
    private void findAndValidateLinks(File file, List<String> regexs) throws IOException {
        List<Link> links = FileUtils.findLinks(
            file,
            regexs,
            Link.ValidationOptions.builder()
                .currentDir(getCurrentDirectory())
                .contentDirectory(contentDirectory)
                .imageDirectory(imageDirectory)
                .indexFilename(indexFilename)
                .allAbsolute(allAbsolute)
                .imageAbsolute(Optional.ofNullable(imageAbsolute).orElse(false))
                .skipExternal(skipExternal)
                .skipMailto(skipMailto)
                .skipRelative(skipRelative)
                .insecure(insecure)
                .build()
        );

        if (links.isEmpty() && docsource.verbose) {
            commandSpec.commandLine().getOut().println(Help.Ansi.AUTO.string("No link found.\n"));
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
            new Help.Column(skipMailto ? 9 : 6, 2, Help.Column.Overflow.SPAN),
            new Help.Column(7, 2, Help.Column.Overflow.SPAN)
        );

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
        textTable.addRowValues(
            "Success",
            displaySuccessRelative,
            displaySuccessExternal,
            displaySuccessMail,
            String.valueOf(successRelative + successExternal + successMail));

        textTable.addRowValues(
            "Broken",
            displayBrokenRelative,
            displayBrokenExternal,
            displayBrokenMail,
            String.valueOf(brokenRelative + brokenExternal + brokenMail));

        textTable.addRowValues(
            "Total",
            String.valueOf(successRelative + brokenRelative),
            String.valueOf(successExternal + brokenExternal),
            String.valueOf(successMail + brokenMail),
            String.valueOf(successRelative
                + successExternal
                + successMail
                + brokenRelative
                + brokenExternal
                + brokenMail)
        );

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
        Path scanFile;
        if (file.isAbsolute()) {
            scanFile = Path.of(file.getAbsolutePath());
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getCurrentDirectory());
            stringBuilder.append(File.separator);
            if (contentDirectory != null) {
                stringBuilder.append(contentDirectory);
                stringBuilder.append(File.separator);
            }
            stringBuilder.append(file);
            scanFile = Path.of(stringBuilder.toString());
        }

        if (scanFile.toFile().isFile()) {
            if (!FileUtils.isAuthorized(scanFile.toFile())) {
                commandSpec.commandLine().getErr()
                    .println("The format of the " + file + " file is not supported.");
                return List.of();
            }
            return List.of(scanFile.toFile());
        }

        commandSpec.commandLine().getOut().println(Help.Ansi.AUTO.string(
            "Scanning directory @|bold " + scanFile + "|@"));

        try {
            List<File> files = FileUtils.findFiles(scanFile.toFile(), recursive);
            commandSpec.commandLine().getOut().println(Help.Ansi.AUTO.string(
                "Found @|bold " + files.size() + " file(s)|@ to scan"));
            return files;
        } catch (IOException e) {
            commandSpec.commandLine().getErr()
                .println("Cannot retrieve files from directory " + scanFile);
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
