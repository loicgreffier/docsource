package io.github.loicgreffier.models.impl;

import io.github.loicgreffier.models.Link;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static io.github.loicgreffier.models.Link.Status.*;
import static java.net.HttpURLConnection.*;

public class ExternalLink extends Link {
    public ExternalLink(File file, String path, String markdown, ValidationOptions validationOptions) {
        super(file, path, markdown, validationOptions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        try {
            HttpClient client = buildHttpClient(validationOptions.isInsecure());

            HttpRequest request = HttpRequest.newBuilder()
                    .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36") // Modify user-agent for websites with protection against Java HTTP clients
                    .setHeader("Accept", "*/*")
                    .uri(URI.create(path))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

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
     * Build a new HTTP client
     * @param insecure Should the HTTP client be secured or not
     * @return A new HTTP client
     * @throws KeyManagementException Thrown when initializing the insecure SSL context
     * @throws NoSuchAlgorithmException Thrown if no algorithm found when HTTP client build
     * @see https://stackoverflow.com/questions/52856027/jdk-11-httpclient-throws-no-subject-alternative-dns-name-error
     */
    public HttpClient buildHttpClient(boolean insecure) throws KeyManagementException, NoSuchAlgorithmException {
        SSLParameters sslParameters = new SSLParameters();
        sslParameters.setUseCipherSuitesOrder(false); // Some websites require to use server ciphers

        HttpClient.Builder builder = HttpClient.newBuilder()
                .sslParameters(sslParameters);

        if (insecure) {
            TrustManager[] insecureTrustManager = new TrustManager[]{
                    new X509ExtendedTrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
                            // No client checks on purpose
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
                            // No server checks on purpose
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
                            // No client checks on purpose
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
                            // No server checks on purpose
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                            // No client checks on purpose
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
