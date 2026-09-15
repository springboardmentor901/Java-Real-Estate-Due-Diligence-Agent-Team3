package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ownership_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnershipRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    private String primaryOwnerName;
    private String secondaryOwnerName;
    private String ownerType; // e.g. INDIVIDUAL, CORPORATION, TRUST
    private String ownershipType; // e.g. FEE_SIMPLE, LEASEHOLD, JOINT_TENANCY
    private LocalDate purchaseDate;
    private String deedType; // e.g. WARRANTY_DEED, QUITCLAIM_DEED
    private String documentNumber;
    private String mailingAddress;

    @Builder.Default
    private LocalDateTime fetchedAt = LocalDateTime.now();
}