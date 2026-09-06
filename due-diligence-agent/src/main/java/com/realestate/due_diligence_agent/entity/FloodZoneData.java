package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FloodZoneData {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "property_id", unique = true)
    private Property property;
    private String floodZone;
    private String zoneDescription;
    private String firmPanel;
    private BigDecimal elevation;
    private String source;
}
