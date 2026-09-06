package com.realestate.due_diligence_agent.dto;

import com.realestate.due_diligence_agent.entity.OwnershipRecord;
import java.time.LocalDate;

public record OwnershipResponse(Long id, String ownerName, String ownershipType, LocalDate acquisitionDate) {
    public static OwnershipResponse from(OwnershipRecord record) {
        return new OwnershipResponse(record.getId(), record.getOwnerName(), record.getOwnershipType(), record.getAcquisitionDate());
    }
}
