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
import java.util.List;

import static io.lgr.docsource.models.Link.Status.*;
import static java.net.HttpURLConnection.*;

public class ExternalLink extends Link {
    public ExternalLink(String link, Path file) {
        super(link, file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
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

            int code = response.statusCode();
            if (code >= HTTP_BAD_REQUEST && !List.of(HTTP_UNAUTHORIZED, HTTP_FORBIDDEN).contains(code)) {
                status = BROKEN;
            } else if (code >= HTTP_MULT_CHOICE && code < HTTP_BAD_REQUEST) {
                status = REDIRECT;
            } else {
                status = SUCCESS;
            }

            details = String.valueOf(code);
        } catch (IllegalArgumentException | URISyntaxException e) {
            status = BROKEN;
            details = e.getMessage();
        } catch (IOException e) {
            status = BROKEN;
            details = "invalid URL";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            status = BROKEN;
            details = e.getMessage();
        }
    }
}
