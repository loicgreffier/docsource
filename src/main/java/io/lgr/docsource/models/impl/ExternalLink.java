package io.lgr.docsource.models.impl;

import io.lgr.docsource.models.Link;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

public class ExternalLink extends Link {
    public ExternalLink(String path, Path file) {
        super(path, file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .header("User-Agent", "Docsource") // Modify user-agent for websites with protection against Java HTTP clients
                    .uri(new URI(path))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(3))
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                status = Status.DEAD;
            } else if (response.statusCode() >= HttpURLConnection.HTTP_MULT_CHOICE) {
                status = Status.REDIRECT;
            } else {
                status = Status.SUCCESS;
            }

            details = String.valueOf(response.statusCode());
        } catch (IllegalArgumentException | URISyntaxException e) {
            status = Status.DEAD;
            details = e.getMessage();
        } catch (IOException e) {
            status = Status.DEAD;
            details = "invalid URL";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            status = Status.DEAD;
            details = e.getMessage();
        }
    }
}
