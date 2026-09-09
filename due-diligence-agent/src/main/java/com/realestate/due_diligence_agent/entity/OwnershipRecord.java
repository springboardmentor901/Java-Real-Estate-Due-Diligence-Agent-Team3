package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OwnershipRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "property_id")
    private Property property;
    private String ownerName;
    private String ownershipType;
    private LocalDate acquisitionDate;
}
