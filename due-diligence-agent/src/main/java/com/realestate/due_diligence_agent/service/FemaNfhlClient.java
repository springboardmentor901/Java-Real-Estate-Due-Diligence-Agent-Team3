package com.realestate.due_diligence_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class FemaNfhlClient {
    private final WebClient webClient;
    @Value("${fema.nfhl.base-url:}")
    private String baseUrl;
    @Value("${fema.nfhl.hazard-layer:}")
    private String hazardLayer;
    @Value("${fema.nfhl.firm-panel-layer:}")
    private String firmPanelLayer;

    public JsonNode query(String layer, double latitude, double longitude) {
        if (baseUrl.isBlank() || layer.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "FEMA NFHL is not configured");
        }
        try {
            return webClient.get().uri(UriComponentsBuilder.fromUriString(baseUrl + "/" + layer + "/query")
                    .queryParam("f", "json").queryParam("where", "1=1")
                    .queryParam("geometry", longitude + "," + latitude)
                    .queryParam("geometryType", "esriGeometryPoint")
                    .queryParam("inSR", "4326").queryParam("spatialRel", "esriSpatialRelIntersects")
                    .queryParam("outFields", "*").queryParam("returnGeometry", "false")
                    .build().toUri()).retrieve().bodyToMono(JsonNode.class).block();
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "FEMA NFHL request failed", ex);
        }
    }

    public String hazardLayer() { return hazardLayer; }
    public String firmPanelLayer() { return firmPanelLayer; }
}
