package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.entity.TaxHistory;
import com.realestate.due_diligence_agent.repository.TaxHistoryRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyValueHistoryService {

    private final TaxHistoryRepository taxHistoryRepository;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValueHistoryResponse {
        private Integer year;
        private BigDecimal assessedValue;
        private BigDecimal landValue;
        private BigDecimal improvementValue;
    }

    public List<ValueHistoryResponse> getValueHistory(Long propertyId) {
        List<TaxHistory> records = taxHistoryRepository.findByPropertyIdOrderByTaxYearDesc(propertyId);
        return records.stream()
                .sorted(Comparator.comparing(TaxHistory::getTaxYear))
                .map(r -> ValueHistoryResponse.builder()
                        .year(r.getTaxYear())
                        .assessedValue(r.getTotalAssessedValue())
                        .landValue(r.getAssessedLandValue())
                        .improvementValue(r.getAssessedImprovementValue())
                        .build())
                .collect(Collectors.toList());
    }
}