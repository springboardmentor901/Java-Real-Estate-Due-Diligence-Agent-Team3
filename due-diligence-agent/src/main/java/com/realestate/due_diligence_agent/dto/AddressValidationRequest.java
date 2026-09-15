package com.realestate.due_diligence_agent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressValidationRequest {

    @NotBlank(message = "Address must not be blank and must contain meaningful information")
    private String address;
}