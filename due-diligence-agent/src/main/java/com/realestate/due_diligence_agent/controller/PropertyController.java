package com.realestate.due_diligence_agent.controller;

import com.realestate.due_diligence_agent.dto.TimelineEntry;
import com.realestate.due_diligence_agent.dto.PropertyResponse;
import com.realestate.due_diligence_agent.dto.AddressValidationRequest;
import com.realestate.due_diligence_agent.dto.AddressValidationResponse;
import com.realestate.due_diligence_agent.dto.OwnershipResponse;
import com.realestate.due_diligence_agent.dto.TaxHistoryResponse;
import com.realestate.due_diligence_agent.dto.ZoningResponse;
import com.realestate.due_diligence_agent.dto.FloodZoneResponse;
import com.realestate.due_diligence_agent.dto.PermitResponse;
import com.realestate.due_diligence_agent.dto.EnvironmentalRecordResponse;
import com.realestate.due_diligence_agent.dto.UtilityRequest;
import com.realestate.due_diligence_agent.dto.UtilityResponse;
import com.realestate.due_diligence_agent.dto.DueDiligenceResponse;
import com.realestate.due_diligence_agent.entity.User;
import com.realestate.due_diligence_agent.service.AddressValidationService;
import com.realestate.due_diligence_agent.service.PropertyDataService;
import com.realestate.due_diligence_agent.service.FloodZoneService;
import com.realestate.due_diligence_agent.service.PermitService;
import com.realestate.due_diligence_agent.service.EnvironmentalRecordService;
import com.realestate.due_diligence_agent.service.UtilityService;
import com.realestate.due_diligence_agent.service.DueDiligenceService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import com.realestate.due_diligence_agent.entity.Property;
import com.realestate.due_diligence_agent.repository.PropertyRepository;
import com.realestate.due_diligence_agent.service.PropertyTimelineBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {
    private final PropertyRepository propertyRepository;
    private final PropertyTimelineBuilder timelineBuilder;
    private final AddressValidationService addressValidationService;
    private final PropertyDataService propertyDataService;
    private final FloodZoneService floodZoneService;
    private final PermitService permitService;
    private final EnvironmentalRecordService environmentalRecordService;
    private final UtilityService utilityService;
    private final DueDiligenceService dueDiligenceService;

    @PostMapping("/validate_address")
    public AddressValidationResponse validateAddress(@Valid @RequestBody AddressValidationRequest request) {
        return addressValidationService.validate(request);
    }

    @GetMapping("/{id}/ownership")
    public List<OwnershipResponse> ownership(@PathVariable Long id) {
        return propertyDataService.ownership(id).stream().map(OwnershipResponse::from).toList();
    }

    @GetMapping("/{id}/tax-history")
    public List<TaxHistoryResponse> taxHistory(@PathVariable Long id) {
        return propertyDataService.taxHistory(id).stream().map(TaxHistoryResponse::from).toList();
    }

    @GetMapping("/{id}/zoning")
    public ZoningResponse zoning(@PathVariable Long id) {
        var zoning = propertyDataService.zoning(id);
        return zoning == null ? null : ZoningResponse.from(zoning);
    }

    @GetMapping("/{id}/flood-zone")
    public FloodZoneResponse floodZone(@PathVariable Long id) {
        return FloodZoneResponse.from(floodZoneService.getOrFetch(id));
    }

    @GetMapping("/{id}/permits")
    public List<PermitResponse> permits(@PathVariable Long id) {
        return permitService.getOrFetch(id).stream().map(PermitResponse::from).toList();
    }

    @GetMapping("/{id}/environmental")
    public List<EnvironmentalRecordResponse> environmental(@PathVariable Long id) {
        return environmentalRecordService.getOrFetch(id).stream().map(EnvironmentalRecordResponse::from).toList();
    }

    @GetMapping("/{id}/utilities")
    public List<UtilityResponse> utilities(@PathVariable Long id) {
        return utilityService.list(id).stream().map(UtilityResponse::from).toList();
    }

    @PostMapping("/{id}/utilities")
    public UtilityResponse createUtility(@PathVariable Long id, @Valid @RequestBody UtilityRequest request,
                                         @AuthenticationPrincipal User user) {
        return UtilityResponse.from(utilityService.create(id, request, user));
    }

    @PutMapping("/{id}/utilities/{utilityId}")
    public UtilityResponse updateUtility(@PathVariable Long id, @PathVariable Long utilityId,
                                         @Valid @RequestBody UtilityRequest request) {
        return UtilityResponse.from(utilityService.update(id, utilityId, request));
    }

    @DeleteMapping("/{id}/utilities/{utilityId}")
    public void deleteUtility(@PathVariable Long id, @PathVariable Long utilityId) {
        utilityService.delete(id, utilityId);
    }

    @GetMapping("/{id}/due-diligence")
    public DueDiligenceResponse dueDiligence(@PathVariable Long id) {
        return dueDiligenceService.aggregate(id);
    }

    @GetMapping
    public List<PropertyResponse> list(@RequestParam(required = false) String address) {
        List<Property> properties = address == null || address.isBlank()
                ? propertyRepository.findAll()
                : propertyRepository.findByAddressContainingIgnoreCaseOrderByAddressAsc(address.trim());
        return properties.stream().map(PropertyResponse::from).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PropertyResponse details(@PathVariable Long id) {
        return PropertyResponse.from(propertyDataService.details(id));
    }

    @GetMapping("/{id}/history")
    public List<TimelineEntry> history(@PathVariable Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
        return timelineBuilder.build(property);
    }
}
