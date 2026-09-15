package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tax_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    private Integer taxYear;
    private BigDecimal assessedLandValue;
    private BigDecimal assessedImprovementValue;
    private BigDecimal totalAssessedValue;
    private BigDecimal totalTaxAmount;
    private String taxStatus; // PAID, DELINQUENT, PENDING
    private String taxingAuthority;

    @Builder.Default
    private LocalDateTime fetchedAt = LocalDateTime.now();
}