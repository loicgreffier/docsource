package io.lgr.docsource.commands;

import io.lgr.docsource.models.Link;
import io.lgr.docsource.models.impl.MailtoLink;
import io.lgr.docsource.models.impl.RelativeLink;
import io.lgr.docsource.models.impl.ExternalLink;
import io.lgr.docsource.utils.FileUtils;
import io.lgr.docsource.utils.VersionProvider;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import static io.lgr.docsource.commands.DocsourceCommand.VERBOSE;
import static io.lgr.docsource.models.Link.Status.*;

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
    @CommandLine.Parameters(description = "Directory or file(s) to scan.")
    public List<File> paths;

    @CommandLine.Option(names = {"-r", "--recursive"}, description = "Scan directories recursively.")
    public boolean recursive;

    @CommandLine.Option(names = {"-c", "--current-dir"}, description = "Override the current directory.")
    public String currentDir;

    @CommandLine.Option(names = {"-s", "--start-with"}, description = "Complete the beginning of inline links with a partial path.")
    public String startWith;

    @Getter
    private final List<Link> scannedLinks = new ArrayList<>();

    /**
     * Execute the "docsource scan" sub command.
     * @return An execution code
     */
    @Override
    public Integer call() {
        if (paths == null) {
            CommandLine docsourceScan = new CommandLine(this);
            docsourceScan.usage(System.out);
            return 0;
        }

        paths.forEach(path -> {
            List<Path> filesToScan = getFilesFromPath(path);
            filesToScan.forEach(file -> {
                System.out.println(CommandLine.Help.Ansi.AUTO.string("Scanning file @|bold " + file.toAbsolutePath() + "|@"));

                try {
                    List<String> links = FileUtils.getLinks(file);
                    if (links.isEmpty()) {
                        System.out.println(CommandLine.Help.Ansi.AUTO.string("No link found.\n"));
                        return;
                    }

                    links.forEach(link -> {
                        Link linkToScan;
                        if (link.contains("://")) {
                            linkToScan = new ExternalLink(link, file);
                        } else if (link.contains("mailto:")) {
                            linkToScan = new MailtoLink(link, file);
                        } else {
                            linkToScan = new RelativeLink(link, file, getCurrentDirectory(), path, startWith);
                        }
                        linkToScan.validate();

                        if (VERBOSE) {
                            System.out.println(CommandLine.Help.Ansi.AUTO.string(linkToScan.toAnsiString()));
                        }
                        scannedLinks.add(linkToScan);
                    });

                    if (VERBOSE) System.out.println();
                } catch (IOException e) {
                    System.err.println("Cannot get links from file " + file + ".");
                }
            });
        });

        long totalExternal = getScannedLinksByType(ExternalLink.class).size();
        long totalRelative = getScannedLinksByType(RelativeLink.class).size();
        long totalMailto = getScannedLinksByType(MailtoLink.class).size();
        long total = totalExternal + totalRelative + totalMailto;
        long totalSuccess = getScannedLinksByStatus(SUCCESS).size();
        long totalRedirect = getScannedLinksByStatus(REDIRECT).size();
        List<Link> brokenLinks = getScannedLinksByStatus(BROKEN);
        long totalBroken = brokenLinks.size();

        if (!VERBOSE) System.out.println();

        System.out.println(CommandLine.Help.Ansi.AUTO.string("Summary"));
        int numberOfHyphens = 30 + String.valueOf(total).length();
        System.out.println(CommandLine.Help.Ansi.AUTO.string(new String(new char[numberOfHyphens]).replace("\0", "-")));
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|bold " + total + "|@ link(s) scanned in total (@|bold " + totalRelative + "|@ relative / @|bold " + totalExternal + "|@ external / @|bold " + totalMailto + "|@ mailto)"));
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|bold " + totalSuccess + "|@ success link(s)"));
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|bold " + totalRedirect + "|@ redirected link(s)"));
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|bold " + totalBroken + "|@ broken link(s)"));

        if (totalBroken > 0) {
            brokenLinks.forEach(brokenLink ->
                    System.out.println(CommandLine.Help.Ansi.AUTO.string("  - " + brokenLink.toAnsiString() + " in file @|bold " + brokenLink.getFile().toAbsolutePath() + "|@")));
        }

        System.out.println("\nEnd scanning... It's been an honour!");
        return totalBroken == 0 ? 0 : 1;
    }

    /**
     * Get all the files at the given path.
     * The path may be a file itself or a folder.
     * @param path The path to get the files
     * @return A list of files
     */
    public List<Path> getFilesFromPath(File path) {
        if (path.isFile()) {
            String fileExtension = FilenameUtils.getExtension(path.toString());
            if (!FileUtils.authorizedFileExtensions().contains(fileExtension)) {
                System.err.println("Cannot scan file with extension " + fileExtension);
                return List.of();
            }
            return List.of(path.toPath());
        }

        System.out.println(CommandLine.Help.Ansi.AUTO.string("Scanning directory @|bold " + path.getAbsolutePath() + "|@"));

        try {
            try (Stream<Path> filesStream = Files.find(Paths.get(path.toURI()), recursive ? Integer.MAX_VALUE : 1,
                            (filePath, fileAttr) -> fileAttr.isRegularFile() && FileUtils.authorizedFileExtensions()
                                    .contains(FilenameUtils.getExtension(filePath.toString())))) {
                List<Path> files = filesStream.toList();
                System.out.println(CommandLine.Help.Ansi.AUTO.string("Found @|bold " + files.size() + " file(s)|@ to scan\n"));
                return files;
            }
        } catch (IOException e) {
            System.err.println("Cannot retrieve files from directory " + path.getAbsolutePath());
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
