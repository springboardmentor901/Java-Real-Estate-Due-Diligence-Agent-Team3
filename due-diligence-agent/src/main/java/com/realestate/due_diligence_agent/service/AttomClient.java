package com.realestate.due_diligence_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class AttomClient {
    private final WebClient webClient;
    @Value("${attom.base-url:}") private String baseUrl;
    @Value("${attom.api-key:}") private String apiKey;

    public boolean isConfigured() {
        return !baseUrl.isBlank() && !apiKey.isBlank();
    }

    public JsonNode get(String path, String address) {
        if (baseUrl.isBlank() || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "ATTOM is not configured");
        }
        try {
            return webClient.get().uri(UriComponentsBuilder.fromUriString(baseUrl + path)
                    .queryParam("address", address).build().toUri())
                    .header("apikey", apiKey).retrieve().bodyToMono(JsonNode.class).block();
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "ATTOM request failed", ex);
        }
    }
}
