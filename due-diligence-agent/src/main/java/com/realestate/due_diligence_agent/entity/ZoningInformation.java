package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ZoningInformation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "property_id", unique = true)
    private Property property;
    private String zoningCode;
    private String landUse;
    private String setbackRequirements;
    private String complianceStatus;
    private String source;
}
