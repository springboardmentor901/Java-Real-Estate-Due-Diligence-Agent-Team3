package com.realestate.due_diligence_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.due_diligence_agent.entity.OwnershipRecord;
import com.realestate.due_diligence_agent.entity.Property;
import com.realestate.due_diligence_agent.entity.TaxHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttomClientService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${attom.api.url:https://api.gateway.attomdata.com}")
    private String attomBaseUrl;

    @Value("${attom.api.key:YOUR_ATTOM_API_KEY_HERE}")
    private String attomApiKey;

    // Fetch Ownership details via ATTOM /property/detailowner
    public List<OwnershipRecord> fetchOwnershipRecords(Property property) {
        if (isMockMode()) {
            return generateMockOwnership(property);
        }

        try {
            String response = webClient.get()
                    .uri(attomBaseUrl + "/propertyapi/v1.0.0/property/detailowner?address1=" 
                            + property.getAddress() + "&address2=" + property.getCity() + ", " + property.getState())
                    .header("apikey", attomApiKey)
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(800)))
                    .block(Duration.ofSeconds(5));

            return parseOwnershipJson(response, property);
        } catch (Exception e) {
            log.error("ATTOM Ownership API call failed: {}. Falling back to default data.", e.getMessage());
            return generateMockOwnership(property);
        }
    }

    // Fetch Tax Assessment details via ATTOM /assessment
    public List<TaxHistory> fetchTaxHistory(Property property) {
        if (isMockMode()) {
            return generateMockTaxHistory(property);
        }

        try {
            String response = webClient.get()
                    .uri(attomBaseUrl + "/propertyapi/v1.0.0/assessment/detail?address1=" 
                            + property.getAddress() + "&address2=" + property.getCity() + ", " + property.getState())
                    .header("apikey", attomApiKey)
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(800)))
                    .block(Duration.ofSeconds(5));

            return parseTaxJson(response, property);
        } catch (Exception e) {
            log.error("ATTOM Tax API call failed: {}. Falling back to default data.", e.getMessage());
            return generateMockTaxHistory(property);
        }
    }

    private boolean isMockMode() {
        return attomApiKey.contains("YOUR_ATTOM") || attomApiKey.isBlank();
    }

    private List<OwnershipRecord> parseOwnershipJson(String json, Property property) {
        List<OwnershipRecord> records = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode propNode = root.path("property").get(0);
            if (propNode != null) {
                JsonNode ownerNode = propNode.path("owner");
                records.add(OwnershipRecord.builder()
                        .property(property)
                        .primaryOwnerName(ownerNode.path("owner1").path("fullName").asText(property.getOwnerName()))
                        .secondaryOwnerName(ownerNode.path("owner2").path("fullName").asText(null))
                        .ownerType(ownerNode.path("corporateIndicator").asText("INDIVIDUAL"))
                        .ownershipType("FEE_SIMPLE")
                        .purchaseDate(LocalDate.now().minusYears(2))
                        .deedType("WARRANTY_DEED")
                        .documentNumber("DOC-" + property.getId() + "-ATTOM")
                        .mailingAddress(ownerNode.path("mailingAddress").path("oneLine").asText(property.getAddress()))
                        .build());
            }
        } catch (Exception ex) {
            log.warn("Could not parse ATTOM JSON: {}", ex.getMessage());
        }
        return records.isEmpty() ? generateMockOwnership(property) : records;
    }

    private List<TaxHistory> parseTaxJson(String json, Property property) {
        List<TaxHistory> histories = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode assessmentNode = root.path("property").get(0).path("assessment");
            if (assessmentNode != null) {
                histories.add(TaxHistory.builder()
                        .property(property)
                        .taxYear(LocalDate.now().getYear() - 1)
                        .assessedLandValue(BigDecimal.valueOf(assessmentNode.path("land").asDouble(100000.0)))
                        .assessedImprovementValue(BigDecimal.valueOf(assessmentNode.path("improvement").asDouble(200000.0)))
                        .totalAssessedValue(BigDecimal.valueOf(assessmentNode.path("market").asDouble(300000.0)))
                        .totalTaxAmount(BigDecimal.valueOf(assessmentNode.path("tax").path("taxAmt").asDouble(4500.0)))
                        .taxStatus("PAID")
                        .taxingAuthority(property.getCity() + " County Assessor")
                        .build());
            }
        } catch (Exception ex) {
            log.warn("Could not parse ATTOM Tax JSON: {}", ex.getMessage());
        }
        return histories.isEmpty() ? generateMockTaxHistory(property) : histories;
    }

    private List<OwnershipRecord> generateMockOwnership(Property property) {
        return List.of(OwnershipRecord.builder()
                .property(property)
                .primaryOwnerName(property.getOwnerName() != null ? property.getOwnerName() : "Current Title Holder")
                .secondaryOwnerName("Joint Holder")
                .ownerType("INDIVIDUAL")
                .ownershipType("FEE_SIMPLE")
                .purchaseDate(LocalDate.of(2021, 6, 15))
                .deedType("SPECIAL_WARRANTY_DEED")
                .documentNumber("DEED-2021-987654")
                .mailingAddress(property.getAddress() + ", " + property.getCity() + ", " + property.getState())
                .build());
    }

    private List<TaxHistory> generateMockTaxHistory(Property property) {
        BigDecimal base = property.getPrice() != null ? property.getPrice() : BigDecimal.valueOf(350000);
        return List.of(
                TaxHistory.builder()
                        .property(property)
                        .taxYear(2025)
                        .assessedLandValue(base.multiply(BigDecimal.valueOf(0.3)))
                        .assessedImprovementValue(base.multiply(BigDecimal.valueOf(0.7)))
                        .totalAssessedValue(base)
                        .totalTaxAmount(base.multiply(BigDecimal.valueOf(0.015)))
                        .taxStatus("PAID")
                        .taxingAuthority(property.getCity() + " Department of Revenue")
                        .build(),
                TaxHistory.builder()
                        .property(property)
                        .taxYear(2024)
                        .assessedLandValue(base.multiply(BigDecimal.valueOf(0.28)))
                        .assessedImprovementValue(base.multiply(BigDecimal.valueOf(0.68)))
                        .totalAssessedValue(base.multiply(BigDecimal.valueOf(0.96)))
                        .totalTaxAmount(base.multiply(BigDecimal.valueOf(0.014)))
                        .taxStatus("PAID")
                        .taxingAuthority(property.getCity() + " Department of Revenue")
                        .build()
        );
    }
}