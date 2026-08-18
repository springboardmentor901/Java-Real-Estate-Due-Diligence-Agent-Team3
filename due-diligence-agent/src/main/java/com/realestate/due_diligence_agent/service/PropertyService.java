package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.entity.Property;
import com.realestate.due_diligence_agent.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public List<Property> searchByAddress(String address) {

        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }

        if (address.trim().length() < 3) {
            throw new IllegalArgumentException("Address must contain at least 3 characters");
        }

        return propertyRepository.findByAddressContainingIgnoreCase(address.trim());
    }
}