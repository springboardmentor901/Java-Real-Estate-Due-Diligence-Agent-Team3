package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "comparable_listings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComparableListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comparable_address", nullable = false)
    private String comparableAddress;

    private Double price;

    @Column(name = "distance_miles")
    private Double distanceMiles;

    @Column(name = "listed_date")
    private LocalDate listedDate;

    private String source;

    private Double squareFeet; // Required for price-per-sqft trend calculations

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
}