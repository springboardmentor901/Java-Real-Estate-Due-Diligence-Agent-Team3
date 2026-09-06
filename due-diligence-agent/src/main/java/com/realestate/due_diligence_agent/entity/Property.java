package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Property {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String address;
    private String city;
    private String state;
    private String postalCode;
    private String geoId;
    private String propertyType;
    private Double latitude;
    private Double longitude;
    private String floodZone;
    private String floodRiskRating;
    private String zoningCompliance;
    private String landUse;
    private String setbackRequirements;
    private LocalDate createdAt;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private List<TaxHistory> taxHistory = new ArrayList<>();
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private List<Permit> permits = new ArrayList<>();
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private List<OwnershipRecord> ownershipRecords = new ArrayList<>();
    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private ZoningInformation zoningInformation;

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = LocalDate.now(); }
}
