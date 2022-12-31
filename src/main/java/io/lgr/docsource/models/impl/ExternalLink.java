package io.lgr.docsource.models.impl;

import io.lgr.docsource.models.Link;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

import static io.lgr.docsource.models.Link.Status.*;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_MULT_CHOICE;

public class ExternalLink extends Link {
    public ExternalLink(File file, String markdown) {
        super(file, markdown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36") // Modify user-agent for websites with protection against Java HTTP clients
                    .setHeader("Accept", "*/*")
                    .uri(URI.create(path))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(3))
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            int code = response.statusCode();
            if (code >= HTTP_BAD_REQUEST) {
                status = BROKEN;
            } else if (code >= HTTP_MULT_CHOICE) {
                status = REDIRECT;
            } else {
                status = SUCCESS;
            }

            details = String.valueOf(code);
        } catch (IllegalArgumentException e) {
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
