package com.realestate.due_diligence_agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoningResponse {
    private Long id;
    private Long propertyId;
    private String parcelId;
    private String zoningCode;
    private String zoningDescription;
    private String permittedUses;
    private String jurisdiction;
    private Double maxBuildingHeightFeet;
    private Double maxLotCoveragePercent;
    private Boolean inFloodZone;
    private String floodZoneCode;
    private LocalDateTime fetchedAt;
}