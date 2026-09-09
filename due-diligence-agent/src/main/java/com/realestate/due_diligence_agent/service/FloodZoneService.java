package com.realestate.due_diligence_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.realestate.due_diligence_agent.entity.*;
import com.realestate.due_diligence_agent.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FloodZoneService {
    private final PropertyRepository propertyRepository;
    private final FloodZoneDataRepository floodRepository;
    private final FemaNfhlClient femaClient;

    @Transactional
    public FloodZoneData getOrFetch(Long propertyId) {
        return floodRepository.findByPropertyId(propertyId).orElseGet(() -> {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
            if (property.getLatitude() == null || property.getLongitude() == null) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Property coordinates are required");
            }
            JsonNode hazard = femaClient.query(femaClient.hazardLayer(), property.getLatitude(), property.getLongitude());
            JsonNode panel = femaClient.query(femaClient.firmPanelLayer(), property.getLatitude(), property.getLongitude());
            JsonNode hazardAttributes = firstAttributes(hazard);
            JsonNode panelAttributes = firstAttributes(panel);
            FloodZoneData data = FloodZoneData.builder().property(property)
                    .floodZone(text(hazardAttributes, "FLD_ZONE", "ZONE", "ZONE_"))
                    .zoneDescription(text(hazardAttributes, "ZONE_SUBTY", "DESCRIPTION"))
                    .firmPanel(text(panelAttributes, "FIRM_PAN", "PANEL", "FIRM_PANEL"))
                    .source("FEMA NFHL").build();
            return floodRepository.save(data);
        });
    }

    private JsonNode firstAttributes(JsonNode root) {
        JsonNode features = root == null ? null : root.path("features");
        return features != null && features.isArray() && !features.isEmpty()
                ? features.get(0).path("attributes") : com.fasterxml.jackson.databind.node.NullNode.getInstance();
    }
    private String text(JsonNode node, String... names) {
        for (String name : names) if (node.hasNonNull(name)) return node.get(name).asText();
        return null;
    }
}
