package io.lgr.docsource.models.impl;

import io.lgr.docsource.models.Link;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static io.lgr.docsource.models.Link.Status.*;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_MULT_CHOICE;

public class ExternalLink extends Link {
    public ExternalLink(File file, String path, String markdown) {
        super(file, path, markdown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .setHeader("User-Agent", "PostmanRuntime/7.29.2") // Modify user-agent for websites with protection against Java HTTP clients
                    .setHeader("Accept", "*/*")
                    .uri(URI.create(path))
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newBuilder()
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
            details = e.getMessage() != null ? e.getMessage() : "invalid URL";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            status = BROKEN;
            details = e.getMessage();
        }
    }
}
