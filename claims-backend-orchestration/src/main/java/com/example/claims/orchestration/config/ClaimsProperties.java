package com.example.claims.orchestration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "claims")
public class ClaimsProperties {

    private List<String> officers = new ArrayList<>();

    public List<String> getOfficers() {
        return officers;
    }

    public void setOfficers(List<String> officers) {
        this.officers = officers;
    }
}
