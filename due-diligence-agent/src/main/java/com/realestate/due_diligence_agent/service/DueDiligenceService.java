package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.*;
import com.realestate.due_diligence_agent.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class DueDiligenceService {
    private final PropertyDataService propertyDataService;
    private final FloodZoneService floodZoneService;
    private final PermitService permitService;
    private final EnvironmentalRecordService environmentalRecordService;
    private final UtilityService utilityService;
    private final PropertyRepository propertyRepository;

    public DueDiligenceResponse aggregate(Long propertyId) {
        if (!propertyRepository.existsById(propertyId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found");
        }
        CompletableFuture<DueDiligenceSection> ownership = run(() -> propertyDataService.ownership(propertyId)
                .stream().map(OwnershipResponse::from).toList());
        CompletableFuture<DueDiligenceSection> taxHistory = run(() -> propertyDataService.taxHistory(propertyId)
                .stream().map(TaxHistoryResponse::from).toList());
        CompletableFuture<DueDiligenceSection> zoning = run(() -> {
            var value = propertyDataService.zoning(propertyId);
            return value == null ? null : ZoningResponse.from(value);
        });
        CompletableFuture<DueDiligenceSection> floodZone = run(() -> FloodZoneResponse.from(floodZoneService.getOrFetch(propertyId)));
        CompletableFuture<DueDiligenceSection> permits = run(() -> permitService.getOrFetch(propertyId)
                .stream().map(PermitResponse::from).toList());
        CompletableFuture<DueDiligenceSection> environmental = run(() -> environmentalRecordService.getOrFetch(propertyId)
                .stream().map(EnvironmentalRecordResponse::from).toList());
        CompletableFuture<DueDiligenceSection> utilities = run(() -> utilityService.list(propertyId)
                .stream().map(UtilityResponse::from).toList());

        CompletableFuture.allOf(ownership, taxHistory, zoning, floodZone, permits, environmental, utilities).join();
        return new DueDiligenceResponse(propertyId, ownership.join(), taxHistory.join(), zoning.join(),
                floodZone.join(), permits.join(), environmental.join(), utilities.join());
    }

    private CompletableFuture<DueDiligenceSection> run(java.util.function.Supplier<Object> operation) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return DueDiligenceSection.success(operation.get());
            } catch (Throwable throwable) {
                return DueDiligenceSection.failure(throwable);
            }
        });
    }
}
