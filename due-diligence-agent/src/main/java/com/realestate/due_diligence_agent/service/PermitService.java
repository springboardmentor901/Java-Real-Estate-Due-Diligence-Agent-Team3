package com.realestate.due_diligence_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.realestate.due_diligence_agent.entity.*;
import com.realestate.due_diligence_agent.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PermitService {
    private final PropertyRepository propertyRepository;
    private final PermitRepository permitRepository;
    private final ShovelsClient shovelsClient;

    @Transactional
    public List<Permit> getOrFetch(Long propertyId) {
        List<Permit> existing = permitRepository.findByPropertyIdOrderByIssueDateAsc(propertyId);
        if (!existing.isEmpty()) return existing;
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
        JsonNode root = shovelsClient.search(property.getGeoId(), property.getPropertyType());
        JsonNode records = root == null ? null : root.has("data") ? root.get("data") : root.path("permits");
        if (records == null || !records.isArray()) return List.of();
        List<Permit> permits = new ArrayList<>();
        for (JsonNode item : records) {
            permits.add(Permit.builder().property(property)
                    .permitType(text(item, "permit_type", "type"))
                    .permitNumber(text(item, "permit_number", "number"))
                    .description(text(item, "description", "work_description"))
                    .status(text(item, "status"))
                    .issueDate(date(item, "issue_date", "issued_date"))
                    .expiryDate(date(item, "expiry_date", "expiration_date")).build());
        }
        return permitRepository.saveAll(permits);
    }
    private String text(JsonNode node, String... names) {
        for (String name : names) if (node.hasNonNull(name)) return node.get(name).asText();
        return null;
    }
    private LocalDate date(JsonNode node, String... names) {
        String value = text(node, names);
        if (value == null || value.isBlank()) return null;
        try { return LocalDate.parse(value.substring(0, Math.min(10, value.length()))); }
        catch (RuntimeException ignored) { return null; }
    }
}
