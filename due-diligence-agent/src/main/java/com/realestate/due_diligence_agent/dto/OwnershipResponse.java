package com.realestate.due_diligence_agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnershipResponse {
    private Long id;
    private Long propertyId;
    private String primaryOwnerName;
    private String secondaryOwnerName;
    private String ownerType;
    private String ownershipType;
    private LocalDate purchaseDate;
    private String deedType;
    private String documentNumber;
    private String mailingAddress;
    private LocalDateTime fetchedAt;
}