package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.PropertyHistoryResponse;
import com.realestate.due_diligence_agent.entity.Property;
import com.realestate.due_diligence_agent.entity.PropertyHistory;
import com.realestate.due_diligence_agent.repository.PropertyHistoryRepository;
import com.realestate.due_diligence_agent.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyHistoryRepository propertyHistoryRepository;

    // 1. List all properties in the database
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // 2. Search properties by address keyword
    public List<Property> searchPropertiesByAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return propertyRepository.findAll();
        }
        return propertyRepository.findByAddressContainingIgnoreCase(address.trim());
    }

    // 3. Property details by ID
    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    // 4. Property history (returns empty list [] if no history is available)
    public List<PropertyHistoryResponse> getPropertyHistory(Long propertyId) {
        List<PropertyHistory> historyList = propertyHistoryRepository.findByPropertyIdOrderByEventDateDesc(propertyId);
        if (historyList == null || historyList.isEmpty()) {
            return Collections.emptyList();
        }
        return historyList.stream()
                .map(this::mapToHistoryResponse)
                .collect(Collectors.toList());
    }

    private PropertyHistoryResponse mapToHistoryResponse(PropertyHistory history) {
        return PropertyHistoryResponse.builder()
                .id(history.getId())
                .propertyId(history.getProperty().getId())
                .eventDate(history.getEventDate())
                .eventType(history.getEventType())
                .price(history.getPrice())
                .description(history.getDescription())
                .recordedBy(history.getRecordedBy())
                .build();
    }
}