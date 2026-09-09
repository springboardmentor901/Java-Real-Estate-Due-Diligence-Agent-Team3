package com.realestate.due_diligence_agent.dto;

import com.realestate.due_diligence_agent.entity.Permit;
import java.time.LocalDate;

public record PermitResponse(Long id, String permitType, String permitNumber, String description,
                             String status, LocalDate issueDate, LocalDate expiryDate) {
    public static PermitResponse from(Permit permit) {
        return new PermitResponse(permit.getId(), permit.getPermitType(), permit.getPermitNumber(),
                permit.getDescription(), permit.getStatus(), permit.getIssueDate(), permit.getExpiryDate());
    }
}
