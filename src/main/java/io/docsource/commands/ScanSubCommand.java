package io.docsource.commands;

import java.io.File;

import io.docsource.models.Link;
import io.docsource.models.ScannedFile;
import io.docsource.utils.FileUtils;
import io.docsource.utils.VersionProvider;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import static io.docsource.commands.DocsourceCommand.VERBOSE;
import static io.docsource.models.Link.Status.*;
import static io.docsource.models.Link.Type.*;

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
    private static final String EMAIL_REGEX = "(.+)@(.+)";

    @CommandLine.Parameters(description = "Directory or file(s) to scan.")
    public List<File> paths;

    @CommandLine.Option(names = {"-r", "--recursive"}, description = "Scan directories recursively.")
    public boolean recursive;

    private final List<ScannedFile> scannedFiles = new ArrayList<>();

    /**
     * Execute the "docsource scan" sub command
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
                        Link.Type type = REMOTE;
                        Link.Status status = SUCCESS;
                        String result = "OK";
                        if (link.contains("://")) {
                            try {
                                HttpRequest request = HttpRequest.newBuilder()
                                        .header("User-Agent", "Docsource") // Modify user-agent for websites with protection against Java HTTP clients
                                        .uri(new URI(link))
                                        .GET()
                                        .build();

                                HttpResponse<String> response = HttpClient.newBuilder()
                                        .connectTimeout(Duration.ofSeconds(3))
                                        .build()
                                        .send(request, HttpResponse.BodyHandlers.ofString());

                                if (response.statusCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                                    status = DEAD;
                                } else if (response.statusCode() >= HttpURLConnection.HTTP_MULT_CHOICE) {
                                    status = REDIRECT;
                                }

                                result = String.valueOf(response.statusCode());
                            } catch (IllegalArgumentException | URISyntaxException e) {
                                status = DEAD;
                                result = e.getMessage();
                            } catch (IOException e) {
                                status = DEAD;
                                result = "invalid URL";
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                status = DEAD;
                                result = e.getMessage();
                            }
                        } else if (link.contains("mailto:")) {
                            type = EMAIL;
                            if (!link.substring(link.indexOf("mailto:")).matches(EMAIL_REGEX)) {
                                status = DEAD;
                                result = "file not found";
                            }
                        } else {
                            type = LOCAL;
                            if (!Files.exists(Paths.get("./" + link))) {
                                status = DEAD;
                                result = "file not found";
                            }
                        }

                        Link scannedLink = new Link(link, status, result, type);
                        if (VERBOSE) {
                            System.out.println(CommandLine.Help.Ansi.AUTO.string(scannedLink.toAnsiString()));
                        }
                        updateScannedFiles(file, scannedLink);
                    });

                    if (VERBOSE) System.out.println();
                } catch (IOException e) {
                    System.err.println("Cannot get links from file " + file + ".");
                }
            });
        });

        long totalRemote = scannedFiles.stream().flatMap(scannedFile -> scannedFile.getLinks().stream().filter(link -> REMOTE.equals(link.getType()))).count();
        long totalLocal = scannedFiles.stream().flatMap(scannedFile -> scannedFile.getLinks().stream().filter(link -> LOCAL.equals(link.getType()))).count();
        long totalEmail = scannedFiles.stream().flatMap(scannedFile -> scannedFile.getLinks().stream().filter(link -> EMAIL.equals(link.getType()))).count();
        long total = totalRemote + totalLocal + totalEmail;
        long totalSuccess = scannedFiles.stream().flatMap(scannedFile -> scannedFile.getLinks().stream().filter(link -> SUCCESS.equals(link.getStatus()))).count();
        long totalRedirect = scannedFiles.stream().flatMap(scannedFile -> scannedFile.getLinks().stream().filter(link -> REDIRECT.equals(link.getStatus()))).count();
        long totalDead = scannedFiles.stream().flatMap(scannedFile -> scannedFile.getLinks().stream().filter(link -> DEAD.equals(link.getStatus()))).count();

        if (!VERBOSE) System.out.println();
        System.out.println(CommandLine.Help.Ansi.AUTO.string("Summary"));
        int numberOfHyphens = 29 + String.valueOf(total).length();
        System.out.println(CommandLine.Help.Ansi.AUTO.string(new String(new char[numberOfHyphens]).replace("\0", "-")));
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|bold " + total + "|@ link(s) scanned in total (@|bold " + totalLocal + "|@ local / @|bold " + totalRemote + "|@ remote / @|bold " + totalEmail + "|@ email)"));
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|bold " + totalSuccess + "|@ success link(s)"));
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|bold " + totalRedirect + "|@ redirected link(s)"));
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|bold " + totalDead + "|@ dead link(s)\n"));

        if (totalDead > 0) {
            System.out.println("List of dead links");
            System.out.println(CommandLine.Help.Ansi.AUTO.string(new String(new char[numberOfHyphens]).replace("\0", "-")));

            Stream<ScannedFile> filesWithDeadLinks = scannedFiles
                    .stream()
                    .filter(scannedFile -> scannedFile.getLinks().stream().anyMatch(link -> DEAD.equals(link.getStatus())));

            filesWithDeadLinks.forEach(file -> {
                System.out.println(CommandLine.Help.Ansi.AUTO.string(file.toAnsiString()));
                file.getLinks()
                        .stream()
                        .filter(link -> DEAD.equals(link.getStatus()))
                        .forEach(deadLink -> System.out.println(CommandLine.Help.Ansi.AUTO.string("  - " + deadLink.toAnsiString())));
            });
            System.out.println();
        }

        System.out.println("End scanning... It's been an honour!");
        return totalDead == 0 ? 0 : 1;
    }

    private List<Path> getFilesFromPath(File path) {
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

    private void updateScannedFiles(Path file, Link link) {
        Optional<ScannedFile> scannedFileOptional = scannedFiles.stream().filter(scannedFile -> scannedFile.getName().equals(file.toString())).findFirst();
        if (scannedFileOptional.isEmpty()) {
            scannedFiles.add(new ScannedFile(file.toString(), new ArrayList<>(Collections.singletonList(link))));
        } else {
            ScannedFile scannedFile = scannedFileOptional.get();
            scannedFile.getLinks().add(link);
        }
    }
}
