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
public class EpaEnvirofactsClient {
    private final WebClient webClient;
    @Value("${epa.envirofacts.base-url:}") private String baseUrl;
    @Value("${epa.envirofacts.endpoint:frs}") private String endpoint;

    public JsonNode search(String address, String postalCode, int offset, int limit) {
        if (baseUrl.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "EPA Envirofacts is not configured");
        }
        try {
            return webClient.get().uri(UriComponentsBuilder.fromUriString(baseUrl + "/" + endpoint)
                    .queryParam("ADDRESS", address).queryParam("ZIP_CODE", postalCode)
                    .queryParam("offset", offset).queryParam("limit", limit)
                    .build().toUri()).retrieve().bodyToMono(JsonNode.class).block();
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "EPA Envirofacts request failed", ex);
        }
    }
}
