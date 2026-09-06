package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EnvironmentalRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "property_id")
    private Property property;
    private String externalId;
    private String recordType;
    private String facilityName;
    private String address;
    private String status;
    private String source;
}
