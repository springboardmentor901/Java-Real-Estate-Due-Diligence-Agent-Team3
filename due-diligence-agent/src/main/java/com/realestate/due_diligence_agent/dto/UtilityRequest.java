package com.realestate.due_diligence_agent.dto;

import jakarta.validation.constraints.NotBlank;

public record UtilityRequest(@NotBlank String utilityType, String provider, String accountReference, String status) {}
