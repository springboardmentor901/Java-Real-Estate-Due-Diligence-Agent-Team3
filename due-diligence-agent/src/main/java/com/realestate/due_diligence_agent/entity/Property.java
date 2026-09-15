package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String address;

    private String city;
    private String state;
    private String zipCode;

    private Double latitude;
    private Double longitude;

    private String propertyType; // RESIDENTIAL, COMMERCIAL, INDUSTRIAL
    private BigDecimal price;
    private Integer bedrooms;
    private Integer bathrooms;
    private Double squareFeet;
    private Integer yearBuilt;
    private String ownerName;

    @Column(length = 1000)
    private String description;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}