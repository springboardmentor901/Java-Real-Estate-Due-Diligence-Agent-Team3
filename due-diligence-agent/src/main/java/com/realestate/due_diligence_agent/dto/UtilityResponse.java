package com.realestate.due_diligence_agent.dto;

import com.realestate.due_diligence_agent.entity.UtilityInformation;

public record UtilityResponse(Long id, Long propertyId, String utilityType, String provider,
                              String accountReference, String status, String source) {
    public static UtilityResponse from(UtilityInformation utility) {
        return new UtilityResponse(utility.getId(), utility.getProperty().getId(), utility.getUtilityType(),
                utility.getProvider(), utility.getAccountReference(), utility.getStatus(), utility.getSource());
    }
}
