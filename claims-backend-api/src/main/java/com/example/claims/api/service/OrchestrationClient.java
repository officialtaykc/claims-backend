package com.example.claims.api.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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

    public ResponseEntity<String> forwardMultipart(HttpServletRequest request,
                                                   Map<String, String> jsonParts,
                                                   Map<String, String> textParts,
                                                   List<MultipartFile> files) {
        String path = internalPath(request);
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        jsonParts.forEach((name, json) -> bodyBuilder.part(name,
                        new ByteArrayResource(json.getBytes(StandardCharsets.UTF_8)) {
                            @Override
                            public String getFilename() {
                                return name + ".json";
                            }
                        })
                .contentType(MediaType.APPLICATION_JSON));
        textParts.forEach(bodyBuilder::part);
        if (files != null) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }
                var part = bodyBuilder.part("files", file.getResource())
                        .filename(file.getOriginalFilename());
                if (file.getContentType() != null) {
                    part.contentType(MediaType.parseMediaType(file.getContentType()));
                }
            }
        }

        try {
            return restClient.post()
                    .uri(URI.create(baseUrl + path))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(bodyBuilder.build())
                    .exchange((req, response) -> {
                        byte[] bytes = readBody(response.getBody());
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

    public ResponseEntity<byte[]> forwardBytes(HttpServletRequest request) {
        String path = internalPath(request);
        try {
            return restClient.get()
                    .uri(URI.create(baseUrl + path))
                    .exchange((req, response) -> {
                        byte[] bytes = readBody(response.getBody());
                        ResponseEntity.BodyBuilder result = ResponseEntity.status(response.getStatusCode());
                        MediaType contentType = response.getHeaders().getContentType();
                        if (contentType != null) {
                            result.contentType(contentType);
                        }
                        String disposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
                        if (disposition != null) {
                            result.header(HttpHeaders.CONTENT_DISPOSITION, disposition);
                        }
                        return result.body(bytes);
                    });
        } catch (RestClientException e) {
            String error = "{\"error\":\"Bad Gateway\",\"message\":\"Orchestration service unavailable: "
                    + e.getMessage() + "\"}";
            return ResponseEntity.status(502).contentType(MediaType.APPLICATION_JSON)
                    .body(error.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String internalPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/api")) {
            path = "/internal" + path.substring(4);
        }
        String query = request.getQueryString();
        return path + (query != null ? "?" + query : "");
    }

    private byte[] readBody(InputStream inputStream) {
        try (InputStream in = inputStream) {
            return in.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
