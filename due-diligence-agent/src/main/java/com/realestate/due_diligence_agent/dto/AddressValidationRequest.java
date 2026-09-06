package com.realestate.due_diligence_agent.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressValidationRequest(@NotBlank(message = "Address is required") String address, Long propertyId) {}
