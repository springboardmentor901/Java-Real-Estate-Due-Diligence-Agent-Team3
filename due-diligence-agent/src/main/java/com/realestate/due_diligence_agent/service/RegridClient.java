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
public class RegridClient {
    private final WebClient webClient;
    @Value("${regrid.base-url:}") private String baseUrl;
    @Value("${regrid.api-key:}") private String apiKey;

    public JsonNode parcel(double latitude, double longitude) {
        if (baseUrl.isBlank() || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Regrid is not configured");
        }
        try {
            return webClient.get().uri(UriComponentsBuilder.fromUriString(baseUrl)
                    .queryParam("lat", latitude).queryParam("lon", longitude)
                    .queryParam("token", apiKey).build().toUri())
                    .retrieve().bodyToMono(JsonNode.class).block();
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Regrid request failed", ex);
        }
    }
}
