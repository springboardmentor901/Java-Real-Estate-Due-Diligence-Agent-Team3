package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.NominatimResponse;
import com.realestate.due_diligence_agent.entity.Property;
import com.realestate.due_diligence_agent.integration.client.PropertyApiClient;
import com.realestate.due_diligence_agent.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyApiClient propertyApiClient;

    public List<Property> searchByAddress(String address) {

        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }

        String cleanedAddress = address.trim();

        if (cleanedAddress.length() < 3) {
            throw new IllegalArgumentException(
                    "Address must contain at least 3 characters"
            );
        }

        // Validate the address using the external Nominatim API
        NominatimResponse validatedAddress =
                propertyApiClient.validateAddress(cleanedAddress);

        System.out.println(
                "Validated Address: " + validatedAddress.getDisplayName()
        );

        System.out.println(
                "Latitude: " + validatedAddress.getLat()
        );

        System.out.println(
                "Longitude: " + validatedAddress.getLon()
        );

        // Search our local database
        return propertyRepository
                .findByAddressContainingIgnoreCase(cleanedAddress);
    }
}