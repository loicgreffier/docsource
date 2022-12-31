package io.lgr.docsource.commands;

import io.lgr.docsource.models.Link;
import io.lgr.docsource.models.impl.ExternalLink;
import io.lgr.docsource.models.impl.MailtoLink;
import io.lgr.docsource.models.impl.RelativeLink;
import io.lgr.docsource.utils.FileUtils;
import io.lgr.docsource.utils.VersionProvider;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static io.lgr.docsource.models.Link.Status.*;

@Slf4j
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
public class ScanSubCommand implements Callable<Integer> {
    @CommandLine.Parameters(paramLabel = "files", description = "Directories or files to scan.")
    public List<File> inputFiles;

    @CommandLine.Option(names = {"-A", "--all-absolute"}, description = "Consider relative link paths as absolute paths.")
    public boolean allAbsolute;

    @CommandLine.Option(names = {"-c", "--current-dir"}, description = "Override the current directory.")
    public String currentDir;

    @CommandLine.Option(names = {"-p", "--path-prefix"}, description = "Prefix the beginning of relative links with a partial path.")
    public String pathPrefix;

    @CommandLine.Option(names = {"-r", "--recursive"}, description = "Scan directories recursively.")
    public boolean recursive;

    @Getter
    private final List<Link> scannedLinks = new ArrayList<>();

    /**
     * Execute the "docsource scan" sub command.
     * @return An execution code
     */
    @Override
    public Integer call() {
        if (inputFiles == null) {
            CommandLine docsourceScan = new CommandLine(this);
            docsourceScan.usage(System.out);
            return 0;
        }

        inputFiles.forEach(inputFile -> {
            List<File> files = findFiles(inputFile);

            files.forEach(file -> {
                log.info(CommandLine.Help.Ansi.AUTO.string("Scanning file @|bold " + file.getAbsolutePath() + "|@"));

                try {
                    List<String> links = FileUtils.findLinks(file);
                    if (links.isEmpty()) {
                        log.debug(CommandLine.Help.Ansi.AUTO.string("No link found.\n"));
                        return;
                    }

                    links.forEach(markdownLink -> {
                        Link link;
                        if (markdownLink.contains("://")) {
                            link = new ExternalLink(file, markdownLink);
                        } else if (markdownLink.contains("mailto:")) {
                            link = new MailtoLink(file, markdownLink);
                        } else {
                            link = new RelativeLink(file, markdownLink, getCurrentDirectory(), pathPrefix, allAbsolute);
                        }
                        link.validate();

                        log.debug(CommandLine.Help.Ansi.AUTO.string(link.toAnsiString()));

                        scannedLinks.add(link);
                    });

                    log.debug("");
                } catch (IOException e) {
                    log.error("Cannot get links from file {}.", file);
                }
            });
            if (!log.isDebugEnabled()) log.info("");
        });

        long totalExternal = getScannedLinksByType(ExternalLink.class).size();
        long totalRelative = getScannedLinksByType(RelativeLink.class).size();
        long totalMailto = getScannedLinksByType(MailtoLink.class).size();
        long total = totalExternal + totalRelative + totalMailto;
        long totalSuccess = getScannedLinksByStatus(SUCCESS).size();
        long totalRedirect = getScannedLinksByStatus(REDIRECT).size();
        List<Link> brokenLinks = getScannedLinksByStatus(BROKEN);
        long totalBroken = brokenLinks.size();

        log.info(CommandLine.Help.Ansi.AUTO.string("Summary"));
        int hyphenCount = 59 + String.valueOf(total).length() + String.valueOf(totalExternal).length()
                + String.valueOf(totalRelative).length() + String.valueOf(totalMailto).length();
        log.info(CommandLine.Help.Ansi.AUTO.string(new String(new char[hyphenCount]).replace("\0", "-")));
        log.info(CommandLine.Help.Ansi.AUTO.string("@|bold " + total + "|@ link(s) scanned in total (@|bold " + totalRelative + "|@ relative / @|bold " + totalExternal + "|@ external / @|bold " + totalMailto + "|@ mailto)"));
        log.info(CommandLine.Help.Ansi.AUTO.string("@|bold " + totalSuccess + "|@ success link(s)"));
        log.info(CommandLine.Help.Ansi.AUTO.string("@|bold " + totalRedirect + "|@ redirected link(s)"));
        log.info(CommandLine.Help.Ansi.AUTO.string("@|bold " + totalBroken + "|@ broken link(s)"));

        brokenLinks
                .stream()
                .collect(Collectors.groupingBy(link -> link.getFile().getAbsolutePath()))
                .forEach((key, value) -> {
                    log.info(CommandLine.Help.Ansi.AUTO.string("  @|bold " + key + "|@"));
                    value.forEach(brokenLink -> System.out.println(CommandLine.Help.Ansi.AUTO.string("    - " + brokenLink.toAnsiString())));
                });

        log.info("\nEnd scanning... It's been an honour!\n");
        return totalBroken == 0 ? 0 : 1;
    }

    /**
     * Find all files in the given file.
     * The file can be any regular file or a directory.
     * @param file The file
     * @return A list of files
     */
    public List<File> findFiles(File file) throws IOException {
        if (file.isFile()) {
            if (!FileUtils.isAuthorized(file)) {
                log.error("The format of the {} file is not supported.", file);
                return List.of();
            }
            return List.of(file);
        }

        log.info(CommandLine.Help.Ansi.AUTO.string("Scanning directory @|bold " + System.getProperty("user.dir") + "|@"));
        log.info(CommandLine.Help.Ansi.AUTO.string("Scanning directory @|bold " + new File(".") + "|@"));
        log.info(CommandLine.Help.Ansi.AUTO.string("Scanning directory @|bold " + file.getCanonicalPath() + "|@"));
        log.info(CommandLine.Help.Ansi.AUTO.string("Scanning directory @|bold " + file.toURI() + "|@"));
        log.info(CommandLine.Help.Ansi.AUTO.string("Scanning directory @|bold " + file + "|@"));
        log.info(CommandLine.Help.Ansi.AUTO.string("Scanning directory @|bold " + file.getAbsolutePath() + "|@"));

        try {
            List<File> files = FileUtils.findFiles(file, recursive);
            log.info(CommandLine.Help.Ansi.AUTO.string("Found @|bold " + files.size() + " file(s)|@ to scan"));
            return files;
        } catch (IOException e) {
            log.error("Cannot retrieve files from directory " + file.getAbsolutePath());
        }

        return List.of();
    }

    /**
     * Get the scanned links by type
     * @param linkClass The link class
     * @return A list of links
     * @param <T> Any type of link
     */
    public <T extends Link> List<Link> getScannedLinksByType(Class<T> linkClass) {
        return scannedLinks
                .stream()
                .filter(linkClass::isInstance)
                .toList();
    }

    /**
     * Get the scanned links by status
     * @param status The link status
     * @return A list of links
     */
    public List<Link> getScannedLinksByStatus(Link.Status status) {
        return scannedLinks
                .stream()
                .filter(link -> status.equals(link.getStatus()))
                .toList();
    }

    /**
     * Get the user current directory
     * @return The user current directory
     */
    public String getCurrentDirectory() {
        if (currentDir != null) {
            return currentDir;
        }

        return System.getProperty("user.dir");
    }
}
