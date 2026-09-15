package com.realestate.due_diligence_agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxHistoryResponse {
    private Long id;
    private Long propertyId;
    private Integer taxYear;
    private BigDecimal assessedLandValue;
    private BigDecimal assessedImprovementValue;
    private BigDecimal totalAssessedValue;
    private BigDecimal totalTaxAmount;
    private String taxStatus;
    private String taxingAuthority;
    private LocalDateTime fetchedAt;
}