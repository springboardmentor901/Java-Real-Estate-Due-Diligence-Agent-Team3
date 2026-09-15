package com.realestate.due_diligence_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.due_diligence_agent.entity.Property;
import com.realestate.due_diligence_agent.entity.ZoningInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegridClientService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${regrid.api.url:https://app.regrid.com/api/v1}")
    private String regridBaseUrl;

    @Value("${regrid.api.token:YOUR_REGRID_TOKEN_HERE}")
    private String regridToken;

    // Fetch Parcel & Zoning details via REGRID /parcels lookup endpoint
    public ZoningInformation fetchZoningInformation(Property property) {
        if (isMockMode()) {
            return generateMockZoning(property);
        }

        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("app.regrid.com")
                            .path("/api/v1/parcels/point")
                            .queryParam("lat", property.getLatitude() != null ? property.getLatitude() : 39.7817)
                            .queryParam("lon", property.getLongitude() != null ? property.getLongitude() : -89.6501)
                            .queryParam("token", regridToken)
                            .build())
                    .header("Authorization", "Bearer " + regridToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(800)))
                    .block(Duration.ofSeconds(5));

            return parseRegridZoningJson(response, property);
        } catch (Exception e) {
            log.error("REGRID Parcel API call failed: {}. Falling back to default data.", e.getMessage());
            return generateMockZoning(property);
        }
    }

    private boolean isMockMode() {
        return regridToken.contains("YOUR_REGRID") || regridToken.isBlank();
    }

    private ZoningInformation parseRegridZoningJson(String json, Property property) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode parcel = root.path("parcels").path("features").get(0).path("properties").path("fields");
            if (parcel != null) {
                return ZoningInformation.builder()
                        .property(property)
                        .parcelId(parcel.path("parcelnumb").asText("PARCEL-" + property.getId()))
                        .zoningCode(parcel.path("zoning").asText("R-1"))
                        .zoningDescription(parcel.path("zoning_description").asText("Single Family Residential"))
                        .permittedUses("Residential Single-Family, Accessory Dwelling Units")
                        .jurisdiction(parcel.path("county").asText(property.getCity() + " County Planning Board"))
                        .maxBuildingHeightFeet(35.0)
                        .maxLotCoveragePercent(40.0)
                        .inFloodZone(false)
                        .floodZoneCode("ZONE X (Minimal Flood Risk)")
                        .build();
            }
        } catch (Exception ex) {
            log.warn("Could not parse REGRID JSON: {}", ex.getMessage());
        }
        return generateMockZoning(property);
    }

    private ZoningInformation generateMockZoning(Property property) {
        boolean isCommercial = "COMMERCIAL".equalsIgnoreCase(property.getPropertyType());
        return ZoningInformation.builder()
                .property(property)
                .parcelId("PIN-" + property.getZipCode() + "-" + property.getId() + "099")
                .zoningCode(isCommercial ? "C-2" : "R-1A")
                .zoningDescription(isCommercial ? "General Commercial District" : "Single-Family Low-Density Residential")
                .permittedUses(isCommercial ? "Retail, Professional Office, Dining" : "Single-Family Detached, Home Occupations")
                .jurisdiction(property.getCity() + " City Zoning Commission")
                .maxBuildingHeightFeet(isCommercial ? 75.0 : 35.0)
                .maxLotCoveragePercent(isCommercial ? 70.0 : 45.0)
                .inFloodZone(false)
                .floodZoneCode("ZONE X (Minimal Flood Risk)")
                .build();
    }
}