package com.realestate.due_diligence_agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyHistoryResponse {

    private Long id;
    private Long propertyId;
    private LocalDate eventDate;
    private String eventType;
    private BigDecimal price;
    private String description;
    private String recordedBy;
}