package com.example.claims.orchestration.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.claims.orchestration.exception.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class ClaimFormOptionsService {

    private static final String INDEX_LOCATION = "classpath:config/_index.json";
    private static final Pattern CONFIG_FILE_NAME = Pattern.compile("[a-zA-Z0-9_-]+\\.json");

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;
    private Map<String, Map<String, List<String>>> optionsByClaimType;

    public ClaimFormOptionsService(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadOptions() {
        try {
            Map<String, Map<String, String>> index = readJson(INDEX_LOCATION, new TypeReference<>() {});
            Map<String, String> manifest = index.get("claimTypes");
            if (manifest == null || manifest.isEmpty()) {
                throw new IllegalStateException(
                        "Claim form config manifest must define at least one claim type");
            }
            Map<String, Map<String, List<String>>> loadedOptions = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : manifest.entrySet()) {
                String claimType = entry.getKey();
                String fileName = entry.getValue();
                if (claimType == null || claimType.isBlank()
                        || fileName == null || !CONFIG_FILE_NAME.matcher(fileName).matches()) {
                    throw new IllegalStateException("Invalid claim form config file name: " + fileName);
                }

                String location = "classpath:config/" + fileName;
                Map<String, List<String>> config = readJson(location, new TypeReference<>() {});
                if (config == null || config.isEmpty()) {
                    throw new IllegalStateException("Claim form config must not be empty: " + fileName);
                }
                List<String> configuredClaimTypes = config.get("claimTypes");
                if (configuredClaimTypes == null || !configuredClaimTypes.contains(claimType)) {
                    throw new IllegalStateException(
                            "Config " + fileName + " must include claim type " + claimType);
                }
                if (config.entrySet().stream()
                        .anyMatch(option -> option.getValue() == null || option.getValue().isEmpty())) {
                    throw new IllegalStateException("Claim form option groups must not be empty in " + fileName);
                }
                Map<String, List<String>> immutableConfig = new LinkedHashMap<>();
                config.forEach((key, values) -> immutableConfig.put(key, List.copyOf(values)));
                loadedOptions.put(claimType, Collections.unmodifiableMap(immutableConfig));
            }
            if (loadedOptions.isEmpty()) {
                throw new IllegalStateException("Claim form config manifest must not be empty");
            }
            optionsByClaimType = Collections.unmodifiableMap(loadedOptions);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not load claim form options from " + INDEX_LOCATION + " or a referenced file", e);
        }
    }

    public Map<String, List<String>> getOptionsByClaimType(String claimType) {
        Map<String, List<String>> options = optionsByClaimType.get(claimType);
        if (options == null) {
            throw new ResourceNotFoundException("No claim form options configured for claim type: " + claimType);
        }
        return options;
    }

    private <T> T readJson(String location, TypeReference<T> type) throws IOException {
        Resource resource = resourceLoader.getResource(location);
        try (var input = resource.getInputStream()) {
            return objectMapper.readValue(input, type);
        }
    }
}
