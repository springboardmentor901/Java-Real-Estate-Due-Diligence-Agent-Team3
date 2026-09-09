package com.realestate.due_diligence_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.realestate.due_diligence_agent.entity.*;
import com.realestate.due_diligence_agent.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EnvironmentalRecordService {
    private final PropertyRepository propertyRepository;
    private final EnvironmentalRecordRepository environmentalRepository;
    private final EpaEnvirofactsClient epaClient;

    @Transactional
    public List<EnvironmentalRecord> getOrFetch(Long propertyId) {
        List<EnvironmentalRecord> existing = environmentalRepository.findByPropertyIdOrderByIdAsc(propertyId);
        if (!existing.isEmpty()) return existing;
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
        List<EnvironmentalRecord> records = new ArrayList<>();
        int offset = 0;
        final int limit = 100;
        for (int page = 0; page < 5; page++) {
            JsonNode root = epaClient.search(property.getAddress(), property.getPostalCode(), offset, limit);
            JsonNode rows = root != null && root.isArray() ? root : root == null ? null : root.path("data");
            if (rows == null || !rows.isArray() || rows.isEmpty()) break;
            for (JsonNode row : rows) {
                records.add(EnvironmentalRecord.builder().property(property)
                        .externalId(text(row, "REGISTRY_ID", "FACILITY_ID", "ID"))
                        .recordType(text(row, "PROGRAM_SYSTEM_ACRONYM", "RECORD_TYPE"))
                        .facilityName(text(row, "FACILITY_NAME", "NAME"))
                        .address(text(row, "LOCATION_ADDRESS", "ADDRESS"))
                        .status(text(row, "STATUS", "FACILITY_STATUS")).source("EPA Envirofacts").build());
            }
            if (rows.size() < limit) break;
            offset += limit;
        }
        return environmentalRepository.saveAll(records);
    }
    private String text(JsonNode row, String... names) {
        for (String name : names) if (row.hasNonNull(name)) return row.get(name).asText();
        return null;
    }
}
