package com.realestate.due_diligence_agent.controller;

import com.realestate.due_diligence_agent.dto.*;
import com.realestate.due_diligence_agent.entity.Property;
import com.realestate.due_diligence_agent.service.AddressValidationService;
import com.realestate.due_diligence_agent.service.PropertyAggregationService;
import com.realestate.due_diligence_agent.service.PropertyService;
import com.realestate.due_diligence_agent.service.PropertyValueHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final AddressValidationService addressValidationService;
    private final PropertyAggregationService propertyAggregationService;
    private final PropertyValueHistoryService propertyValueHistoryService;

    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Property>> searchProperties(
            @RequestParam(name = "address", required = false) String address) {
        return ResponseEntity.ok(propertyService.searchPropertiesByAddress(address));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        return propertyService.getPropertyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/validate_address")
    public ResponseEntity<AddressValidationResponse> validateAddress(
            @Valid @RequestBody AddressValidationRequest request) {
        return ResponseEntity.ok(addressValidationService.validateAddress(request));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<PropertyHistoryResponse>> getPropertyHistory(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyHistory(id));
    }

    @GetMapping("/{id}/ownership")
    public ResponseEntity<List<OwnershipResponse>> getOwnershipRecords(@PathVariable Long id) {
        return ResponseEntity.ok(propertyAggregationService.getOwnershipRecords(id));
    }

    @GetMapping("/{id}/tax-history")
    public ResponseEntity<List<TaxHistoryResponse>> getTaxHistories(@PathVariable Long id) {
        return ResponseEntity.ok(propertyAggregationService.getTaxHistories(id));
    }

    @GetMapping("/{id}/zoning")
    public ResponseEntity<ZoningResponse> getZoningInformation(@PathVariable Long id) {
        return ResponseEntity.ok(propertyAggregationService.getZoningInformation(id));
    }

    @GetMapping("/{id}/value-history")
    public ResponseEntity<List<PropertyValueHistoryService.ValueHistoryResponse>> getValueHistory(@PathVariable Long id) {
        return ResponseEntity.ok(propertyValueHistoryService.getValueHistory(id));
    }
}