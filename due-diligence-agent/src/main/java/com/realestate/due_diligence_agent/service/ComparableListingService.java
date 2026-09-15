package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.client.RapidApiListingClient;
import com.realestate.due_diligence_agent.dto.RapidApiListingResponse;
import com.realestate.due_diligence_agent.entity.ComparableListing;
import com.realestate.due_diligence_agent.entity.Property;
import com.realestate.due_diligence_agent.repository.ComparableListingRepository;
import com.realestate.due_diligence_agent.repository.PropertyRepository;
import com.realestate.due_diligence_agent.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComparableListingService {

    private final ComparableListingRepository comparableListingRepository;
    private final PropertyRepository propertyRepository;
    private final RapidApiListingClient rapidApiListingClient;

    public List<ComparableListing> getOrFetchComparables(Long propertyId) {
        // 1. Check database first to avoid unnecessary external API calls
        List<ComparableListing> existingListings = comparableListingRepository.findByPropertyId(propertyId);
        if (existingListings != null && !existingListings.isEmpty()) {
            return existingListings;
        }

        // 2. Fetch target property for location/coordinates
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + propertyId));

        // 3. Call RapidAPI
        RapidApiListingResponse apiResponse = rapidApiListingClient.fetchComparables(property.getCity(), property.getState());
        List<ComparableListing> newListings = new ArrayList<>();

        if (apiResponse != null && apiResponse.getResults() != null) {
            for (RapidApiListingResponse.ListingItem item : apiResponse.getResults()) {
                // 4. Calculate distance using Haversine formula
                double distance = 0.0;
                if (property.getLatitude() != null && property.getLongitude() != null &&
                    item.getLatitude() != null && item.getLongitude() != null) {
                    distance = DistanceCalculator.calculateMiles(
                            property.getLatitude(), property.getLongitude(),
                            item.getLatitude(), item.getLongitude()
                    );
                }

                ComparableListing listing = ComparableListing.builder()
                        .comparableAddress(item.getAddress())
                        .price(item.getPrice())
                        .distanceMiles(distance)
                        .listedDate(item.getListDate() != null ? LocalDate.parse(item.getListDate().substring(0, 10)) : LocalDate.now())
                        .source(item.getSource() != null ? item.getSource() : "RapidAPI")
                        .squareFeet(item.getSquareFootage())
                        .property(property)
                        .build();

                newListings.add(listing);
            }
            // 5. Save to database
            comparableListingRepository.saveAll(newListings);
        }

        return newListings;
    }
}