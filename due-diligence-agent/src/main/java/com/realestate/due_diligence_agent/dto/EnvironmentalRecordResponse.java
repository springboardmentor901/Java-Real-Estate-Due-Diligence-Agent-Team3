package com.realestate.due_diligence_agent.dto;

import com.realestate.due_diligence_agent.entity.EnvironmentalRecord;

public record EnvironmentalRecordResponse(Long id, String externalId, String recordType, String facilityName,
                                          String address, String status, String source) {
    public static EnvironmentalRecordResponse from(EnvironmentalRecord record) {
        return new EnvironmentalRecordResponse(record.getId(), record.getExternalId(), record.getRecordType(),
                record.getFacilityName(), record.getAddress(), record.getStatus(), record.getSource());
    }
}
