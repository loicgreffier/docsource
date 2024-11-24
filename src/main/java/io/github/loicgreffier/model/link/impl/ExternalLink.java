package io.github.loicgreffier.model.link.impl;

import static io.github.loicgreffier.model.link.Link.Status.BROKEN;
import static io.github.loicgreffier.model.link.Link.Status.SUCCESS;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

import io.github.loicgreffier.model.link.Link;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

/**
 * This class represents an external link.
 */
public class ExternalLink extends Link {
    public ExternalLink(File file, String path, String markdown,
                        ValidationOptions validationOptions) {
        super(file, path, markdown, validationOptions);
    }

    /**
     * Validate the link.
     * Send a GET request to the link and check the response code.
     * If the response code is 404, the link is broken.
     * If the response code is 200, the link is valid.
     */
    @Override
    public void validate() {
        try {
            HttpClient client = buildHttpClient(validationOptions.isInsecure());

            HttpRequest request = HttpRequest.newBuilder()
                // Modify user-agent for websites with protection against Java HTTP clients
                .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                    + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                .setHeader("Accept", "*/*")
                .uri(URI.create(path))
                .GET()
                .build();

            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            int code = response.statusCode();
            if (response.statusCode() == HTTP_NOT_FOUND) {
                status = BROKEN;
            } else {
                status = SUCCESS;
            }

            details = String.valueOf(code);
        } catch (IllegalArgumentException | KeyManagementException | NoSuchAlgorithmException e) {
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

    /**
     * Build an HTTP client.
     * If insecure is true, the client will accept all certificates.
     *
     * @param insecure true if the client should accept all certificates, false otherwise.
     * @return The HTTP client.
     * @throws KeyManagementException   If the key management fails.
     * @throws NoSuchAlgorithmException If the algorithm is not found.
     * @see <a href="https://stackoverflow.com/questions/52856027/jdk-11-httpclient-throws-no-subject-alternative-dns-name-error">StackOverflow answer</a>
     */
    public HttpClient buildHttpClient(boolean insecure)
        throws KeyManagementException, NoSuchAlgorithmException {
        SSLParameters sslParameters = new SSLParameters();
        sslParameters.setUseCipherSuitesOrder(false); // Some websites require to use server ciphers

        HttpClient.Builder builder = HttpClient.newBuilder()
            .sslParameters(sslParameters);

        if (insecure) {
            TrustManager[] insecureTrustManager = new TrustManager[] {
                new X509ExtendedTrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType,
                                                   Socket socket) {
                        // No client checks on purpose
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType,
                                                   SSLEngine engine) {
                        // No client checks on purpose
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        // No client checks on purpose
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType,
                                                   Socket socket) {
                        // No server checks on purpose
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType,
                                                   SSLEngine engine) {
                        // No server checks on purpose
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        // No server checks on purpose
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, insecureTrustManager, null);

            return builder
                .sslContext(sslContext)
                .build();
        }

        return builder
            .build();
    }
}
