package com.realestate.due_diligence_agent.dto;

import com.realestate.due_diligence_agent.entity.Property;
import java.time.LocalDate;

public record PropertyResponse(Long id, String address, String city, String state, String postalCode,
                               Double latitude, Double longitude, String floodZone, String floodRiskRating,
                               String zoningCompliance, String landUse, String setbackRequirements,
                               LocalDate createdAt) {
    public static PropertyResponse from(Property property) {
        return new PropertyResponse(property.getId(), property.getAddress(), property.getCity(),
                property.getState(), property.getPostalCode(), property.getLatitude(), property.getLongitude(),
                property.getFloodZone(), property.getFloodRiskRating(), property.getZoningCompliance(),
                property.getLandUse(), property.getSetbackRequirements(), property.getCreatedAt());
    }
}
