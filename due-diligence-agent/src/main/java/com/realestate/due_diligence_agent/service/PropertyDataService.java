package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.entity.*;
import com.realestate.due_diligence_agent.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
public class PropertyDataService {
    private final PropertyRepository propertyRepository;
    private final OwnershipRecordRepository ownershipRepository;
    private final TaxHistoryRepository taxHistoryRepository;
    private final ZoningInformationRepository zoningRepository;
    private final AttomClient attomClient;
    private final RegridClient regridClient;

    public List<OwnershipRecord> ownership(Long propertyId) {
        requireProperty(propertyId);
        List<OwnershipRecord> records = ownershipRepository.findByPropertyIdOrderByAcquisitionDateAsc(propertyId);
        if (!records.isEmpty()) return records;
        Property property = requireProperty(propertyId);
        JsonNode root = attomClient.get("/property/detailowner", property.getAddress());
        JsonNode rows = array(root, "property", "owners", "data");
        if (rows == null) return List.of();
        List<OwnershipRecord> mapped = new java.util.ArrayList<>();
        for (JsonNode row : rows) mapped.add(OwnershipRecord.builder().property(property)
                .ownerName(text(row, "ownername", "ownerName", "name"))
                .ownershipType(text(row, "ownershiptype", "ownershipType", "type"))
                .acquisitionDate(date(row, "acquisitiondate", "acquisitionDate")).build());
        return ownershipRepository.saveAll(mapped);
    }

    public List<TaxHistory> taxHistory(Long propertyId) {
        requireProperty(propertyId);
        List<TaxHistory> records = taxHistoryRepository.findByPropertyIdOrderByTaxDateAsc(propertyId);
        if (!records.isEmpty()) return records;
        Property property = requireProperty(propertyId);
        JsonNode root = attomClient.get("/assessment", property.getAddress());
        JsonNode rows = array(root, "assessment", "tax", "data");
        if (rows == null) return List.of();
        List<TaxHistory> mapped = new java.util.ArrayList<>();
        for (JsonNode row : rows) mapped.add(TaxHistory.builder().property(property)
                .taxDate(date(row, "taxDate", "taxdate")).dueDate(date(row, "dueDate", "duedate"))
                .amount(decimal(row, "amount", "taxAmount", "taxamt"))
                .paid(!"unpaid".equalsIgnoreCase(text(row, "status", "taxStatus"))).build());
        return taxHistoryRepository.saveAll(mapped);
    }

    public ZoningInformation zoning(Long propertyId) {
        requireProperty(propertyId);
        return zoningRepository.findByPropertyId(propertyId).orElseGet(() -> {
            Property property = requireProperty(propertyId);
            if (property.getLatitude() == null || property.getLongitude() == null) return null;
            JsonNode root = regridClient.parcel(property.getLatitude(), property.getLongitude());
            JsonNode data = root == null ? null : root.has("properties") ? root.get("properties") : root;
            if (data == null || data.isMissingNode()) return null;
            ZoningInformation zoning = ZoningInformation.builder().property(property)
                    .zoningCode(text(data, "zoning", "zoning_code", "zoningCode"))
                    .landUse(text(data, "land_use", "landUse"))
                    .setbackRequirements(text(data, "setback_requirements", "setbackRequirements"))
                    .complianceStatus(text(data, "zoning_compliance", "complianceStatus")).source("Regrid").build();
            property.setZoningCompliance(zoning.getComplianceStatus());
            property.setLandUse(zoning.getLandUse());
            property.setSetbackRequirements(zoning.getSetbackRequirements());
            propertyRepository.save(property);
            return zoningRepository.save(zoning);
        });
    }

    public Property details(Long propertyId) {
        Property property = requireProperty(propertyId);
        if (attomClient.isConfigured() && (property.getCity() == null || property.getState() == null
                || property.getPostalCode() == null || property.getPropertyType() == null)) {
            JsonNode root = attomClient.get("/property/basicprofile", property.getAddress());
            JsonNode data = array(root, "property", "data");
            JsonNode row = data != null && !data.isEmpty() ? data.get(0) : root;
            if (row != null && !row.isMissingNode()) {
                property.setCity(text(row, "city", "address.city"));
                property.setState(text(row, "state", "address.state"));
                property.setPostalCode(text(row, "postalCode", "zip", "address.zip"));
                property.setPropertyType(text(row, "propertyType", "summary.propertyType"));
                propertyRepository.save(property);
            }
        }
        return property;
    }

    private Property requireProperty(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
    }

    private JsonNode array(JsonNode root, String... names) {
        if (root == null) return null;
        for (String name : names) {
            JsonNode node = root.path(name);
            if (node.isArray()) return node;
            if (node.has(name) && node.get(name).isArray()) return node.get(name);
        }
        return root.isArray() ? root : null;
    }
    private String text(JsonNode node, String... names) {
        for (String name : names) if (node.hasNonNull(name)) return node.get(name).asText();
        return null;
    }
    private java.time.LocalDate date(JsonNode node, String... names) {
        String value = text(node, names);
        if (value == null || value.isBlank()) return null;
        try { return java.time.LocalDate.parse(value.substring(0, Math.min(10, value.length()))); }
        catch (RuntimeException ignored) { return null; }
    }
    private java.math.BigDecimal decimal(JsonNode node, String... names) {
        String value = text(node, names);
        try { return value == null ? null : new java.math.BigDecimal(value); }
        catch (NumberFormatException ignored) { return null; }
    }
}
