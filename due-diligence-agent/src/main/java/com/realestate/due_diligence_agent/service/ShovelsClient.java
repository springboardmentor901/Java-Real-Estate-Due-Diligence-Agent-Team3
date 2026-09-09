package com.realestate.due_diligence_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ShovelsClient {
    private final WebClient webClient;
    @Value("${shovels.base-url:}") private String baseUrl;
    @Value("${shovels.api-key:}") private String apiKey;
    @Value("${shovels.permit-from:}") private String permitFrom;
    @Value("${shovels.permit-to:}") private String permitTo;

    public JsonNode search(String geoId, String propertyType) {
        if (baseUrl.isBlank() || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Shovels is not configured");
        }
        if (geoId == null || geoId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Property geo_id is required");
        }
        String from = permitFrom.isBlank() ? LocalDate.now().minusYears(10).toString() : permitFrom;
        String to = permitTo.isBlank() ? LocalDate.now().toString() : permitTo;
        try {
            return webClient.get().uri(UriComponentsBuilder.fromUriString(baseUrl + "/permits/search")
                    .queryParam("geo_id", geoId).queryParam("permit_from", from)
                    .queryParam("permit_to", to).queryParam("property_type", propertyType)
                    .build().toUri()).header("X-API-Key", apiKey)
                    .retrieve().bodyToMono(JsonNode.class).block();
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Shovels permit request failed", ex);
        }
    }
}
