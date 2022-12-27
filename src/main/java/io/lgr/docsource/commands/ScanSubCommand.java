package io.lgr.docsource.commands;

import io.lgr.docsource.models.Link;
import io.lgr.docsource.models.impl.EmailLink;
import io.lgr.docsource.models.impl.InlineLink;
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

    @CommandLine.Option(names = {"-b", "--base"}, description = "Verify inline links from another base folder")
    public String base;

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

        if (VERBOSE) {
            System.out.println("Using current directory " + System.getProperty("user.dir") + " as root directory\n");
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
                            linkToScan = new EmailLink(link, file);
                        } else {
                            linkToScan = new InlineLink(base, link, file);
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
        long totalInline = getScannedLinksByType(InlineLink.class).size();
        long totalEmail = getScannedLinksByType(EmailLink.class).size();
        long total = totalExternal + totalInline + totalEmail;
        long totalSuccess = getScannedLinksByStatus(SUCCESS).size();
        long totalRedirect = getScannedLinksByStatus(REDIRECT).size();
        List<Link> deadLinks = getScannedLinksByStatus(DEAD);
        long totalDead = deadLinks.size();

        if (!VERBOSE) System.out.println();

        System.out.println(CommandLine.Help.Ansi.AUTO.string("Summary"));
        int numberOfHyphens = 30 + String.valueOf(total).length();
        System.out.println(CommandLine.Help.Ansi.AUTO.string(new String(new char[numberOfHyphens]).replace("\0", "-")));
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|bold " + total + "|@ link(s) scanned in total (@|bold " + totalInline + "|@ inline / @|bold " + totalExternal + "|@ external / @|bold " + totalEmail + "|@ email)"));
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|bold " + totalSuccess + "|@ success link(s)"));
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|bold " + totalRedirect + "|@ redirected link(s)"));
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|bold " + totalDead + "|@ dead link(s)"));

        if (totalDead > 0) {
            deadLinks.forEach(deadLink ->
                    System.out.println(CommandLine.Help.Ansi.AUTO.string("  - " + deadLink.toAnsiString() + " in file @|bold " + deadLink.getFile().toAbsolutePath() + "|@")));
        }

        System.out.println("\nEnd scanning... It's been an honour!");
        return totalDead == 0 ? 0 : 1;
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

        try {
            System.out.println(CommandLine.Help.Ansi.AUTO.string("Scanning directory @|bold " + path + "|@"));
            System.out.println(CommandLine.Help.Ansi.AUTO.string("Scanning directory @|bold " + path.getCanonicalPath() + "|@"));
            System.out.println(CommandLine.Help.Ansi.AUTO.string(System.getProperty("user.dir")));
            System.out.println(CommandLine.Help.Ansi.AUTO.string(new File(".").getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
}
