package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.OwnershipResponse;
import com.realestate.due_diligence_agent.dto.TaxHistoryResponse;
import com.realestate.due_diligence_agent.dto.ZoningResponse;
import com.realestate.due_diligence_agent.entity.OwnershipRecord;
import com.realestate.due_diligence_agent.entity.Property;
import com.realestate.due_diligence_agent.entity.TaxHistory;
import com.realestate.due_diligence_agent.entity.ZoningInformation;
import com.realestate.due_diligence_agent.repository.OwnershipRecordRepository;
import com.realestate.due_diligence_agent.repository.PropertyRepository;
import com.realestate.due_diligence_agent.repository.TaxHistoryRepository;
import com.realestate.due_diligence_agent.repository.ZoningInformationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyAggregationService {

    private final PropertyRepository propertyRepository;
    private final OwnershipRecordRepository ownershipRecordRepository;
    private final TaxHistoryRepository taxHistoryRepository;
    private final ZoningInformationRepository zoningInformationRepository;

    private final AttomClientService attomClientService;
    private final RegridClientService regridClientService;

    // 1. Get Ownership Records (Database First -> External ATTOM API Fallback)
    @Transactional
    public List<OwnershipResponse> getOwnershipRecords(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + propertyId));

        List<OwnershipRecord> records = ownershipRecordRepository.findByPropertyIdOrderByPurchaseDateDesc(propertyId);
        if (records.isEmpty()) {
            log.info("No local ownership records found for property {}. Fetching from ATTOM API...", propertyId);
            records = attomClientService.fetchOwnershipRecords(property);
            if (!records.isEmpty()) {
                records = ownershipRecordRepository.saveAll(records);
            }
        }

        return records.stream().map(this::mapToOwnershipResponse).collect(Collectors.toList());
    }

    // 2. Get Tax History (Database First -> External ATTOM API Fallback)
    @Transactional
    public List<TaxHistoryResponse> getTaxHistories(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + propertyId));

        List<TaxHistory> histories = taxHistoryRepository.findByPropertyIdOrderByTaxYearDesc(propertyId);
        if (histories.isEmpty()) {
            log.info("No local tax records found for property {}. Fetching from ATTOM API...", propertyId);
            histories = attomClientService.fetchTaxHistory(property);
            if (!histories.isEmpty()) {
                histories = taxHistoryRepository.saveAll(histories);
            }
        }

        return histories.stream().map(this::mapToTaxResponse).collect(Collectors.toList());
    }

    // 3. Get Zoning Information (Database First -> External REGRID API Fallback)
    @Transactional
    public ZoningResponse getZoningInformation(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + propertyId));

        Optional<ZoningInformation> existing = zoningInformationRepository.findByPropertyId(propertyId);
        ZoningInformation zoning;
        if (existing.isPresent()) {
            zoning = existing.get();
        } else {
            log.info("No local zoning info found for property {}. Fetching from REGRID API...", propertyId);
            zoning = regridClientService.fetchZoningInformation(property);
            zoning = zoningInformationRepository.save(zoning);
        }

        return mapToZoningResponse(zoning);
    }

    private OwnershipResponse mapToOwnershipResponse(OwnershipRecord record) {
        return OwnershipResponse.builder()
                .id(record.getId())
                .propertyId(record.getProperty().getId())
                .primaryOwnerName(record.getPrimaryOwnerName())
                .secondaryOwnerName(record.getSecondaryOwnerName())
                .ownerType(record.getOwnerType())
                .ownershipType(record.getOwnershipType())
                .purchaseDate(record.getPurchaseDate())
                .deedType(record.getDeedType())
                .documentNumber(record.getDocumentNumber())
                .mailingAddress(record.getMailingAddress())
                .fetchedAt(record.getFetchedAt())
                .build();
    }

    private TaxHistoryResponse mapToTaxResponse(TaxHistory history) {
        return TaxHistoryResponse.builder()
                .id(history.getId())
                .propertyId(history.getProperty().getId())
                .taxYear(history.getTaxYear())
                .assessedLandValue(history.getAssessedLandValue())
                .assessedImprovementValue(history.getAssessedImprovementValue())
                .totalAssessedValue(history.getTotalAssessedValue())
                .totalTaxAmount(history.getTotalTaxAmount())
                .taxStatus(history.getTaxStatus())
                .taxingAuthority(history.getTaxingAuthority())
                .fetchedAt(history.getFetchedAt())
                .build();
    }

    private ZoningResponse mapToZoningResponse(ZoningInformation zoning) {
        return ZoningResponse.builder()
                .id(zoning.getId())
                .propertyId(zoning.getProperty().getId())
                .parcelId(zoning.getParcelId())
                .zoningCode(zoning.getZoningCode())
                .zoningDescription(zoning.getZoningDescription())
                .permittedUses(zoning.getPermittedUses())
                .jurisdiction(zoning.getJurisdiction())
                .maxBuildingHeightFeet(zoning.getMaxBuildingHeightFeet())
                .maxLotCoveragePercent(zoning.getMaxLotCoveragePercent())
                .inFloodZone(zoning.getInFloodZone())
                .floodZoneCode(zoning.getFloodZoneCode())
                .fetchedAt(zoning.getFetchedAt())
                .build();
    }
}