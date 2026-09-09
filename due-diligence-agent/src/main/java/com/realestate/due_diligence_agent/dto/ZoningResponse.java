package com.realestate.due_diligence_agent.dto;

import com.realestate.due_diligence_agent.entity.ZoningInformation;

public record ZoningResponse(Long id, String zoningCode, String landUse, String setbackRequirements,
                             String complianceStatus, String source) {
    public static ZoningResponse from(ZoningInformation zoning) {
        return new ZoningResponse(zoning.getId(), zoning.getZoningCode(), zoning.getLandUse(),
                zoning.getSetbackRequirements(), zoning.getComplianceStatus(), zoning.getSource());
    }
}
