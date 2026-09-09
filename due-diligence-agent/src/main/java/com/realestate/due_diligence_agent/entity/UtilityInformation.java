package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UtilityInformation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "property_id")
    private Property property;
    private String utilityType;
    private String provider;
    private String accountReference;
    private String status;
    private String source;
}
