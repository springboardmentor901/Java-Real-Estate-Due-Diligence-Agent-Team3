package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "zoning_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoningInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false, unique = true)
    private Property property;

    private String parcelId;
    private String zoningCode;
    private String zoningDescription;
    private String permittedUses; // e.g. Single-Family, Multi-Family, Light Commercial
    private String jurisdiction;
    private Double maxBuildingHeightFeet;
    private Double maxLotCoveragePercent;
    private Boolean inFloodZone;
    private String floodZoneCode; // e.g. ZONE X, ZONE AE

    @Builder.Default
    private LocalDateTime fetchedAt = LocalDateTime.now();
}