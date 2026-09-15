package com.realestate.due_diligence_agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressValidationResponse {

    private boolean valid;
    private String originalAddress;
    private String formattedAddress;
    private Double latitude;
    private Double longitude;
    private String statusMessage;
}