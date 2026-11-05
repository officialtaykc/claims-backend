package com.example.claims.api.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
public class OrchestrationClient {

    private final RestClient restClient;
    private final String baseUrl;

    public OrchestrationClient(@Qualifier("orchestrationRestClient") RestClient restClient,
                               @Value("${orchestration.base-url}") String baseUrl) {
        this.restClient = restClient;
        this.baseUrl = baseUrl;
    }

    /**
     * Forwards an incoming /api/** request to the orchestration service under /internal/**,
     * preserving the HTTP method, path, query string and JSON body. The orchestration
     * response status and body are passed straight back to the caller.
     */
    public ResponseEntity<String> forward(HttpServletRequest request, String body) {
        String path = request.getRequestURI();
        if (path.startsWith("/api")) {
            path = "/internal" + path.substring(4);
        }
        String query = request.getQueryString();
        String uri = baseUrl + path + (query != null ? "?" + query : "");

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        RestClient.RequestBodySpec spec = restClient.method(method).uri(URI.create(uri));
        if (body != null && !body.isBlank()) {
            spec.contentType(MediaType.APPLICATION_JSON).body(body);
        }

        try {
            return spec.exchange((req, response) -> {
                byte[] bytes;
                try (InputStream in = response.getBody()) {
                    bytes = in.readAllBytes();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                return ResponseEntity.status(response.getStatusCode())
                        .contentType(response.getHeaders().getContentType())
                        .body(new String(bytes, StandardCharsets.UTF_8));
            });
        } catch (RestClientException e) {
            return ResponseEntity.status(502).body(
                    "{\"error\":\"Bad Gateway\",\"message\":\"Orchestration service unavailable: "
                            + e.getMessage() + "\"}");
        }
    }
}
