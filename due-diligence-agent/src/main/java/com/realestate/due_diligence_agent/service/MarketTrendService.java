package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.entity.ComparableListing;
import com.realestate.due_diligence_agent.repository.ComparableListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MarketTrendService {

    private final ComparableListingRepository comparableListingRepository;

    public Map<String, Object> calculateTrends(Long propertyId) {
        List<ComparableListing> listings = comparableListingRepository.findByPropertyId(propertyId);
        
        Map<String, Object> trends = new HashMap<>();
        if (listings == null || listings.isEmpty()) {
            trends.put("averagePrice", 0.0);
            trends.put("averagePricePerSqFt", 0.0);
            trends.put("weightedMarketTrendScore", 0.0);
            return trends;
        }

        double totalPrice = 0;
        double totalPricePerSqFt = 0;
        int sqFtCount = 0;
        
        double weightedPriceSum = 0;
        double totalWeight = 0;
        LocalDate today = LocalDate.now();

        for (ComparableListing listing : listings) {
            double price = listing.getPrice() != null ? listing.getPrice() : 0.0;
            totalPrice += price;

            if (listing.getSquareFeet() != null && listing.getSquareFeet() > 0) {
                totalPricePerSqFt += (price / listing.getSquareFeet());
                sqFtCount++;
            }

            // Recency-weighted calculation (listings within 30 days get higher importance)
            long daysAgo = 1;
            if (listing.getListedDate() != null) {
                daysAgo = Math.max(1, ChronoUnit.DAYS.between(listing.getListedDate(), today));
            }
            double weight = 1.0 / Math.sqrt(daysAgo); // More recent = higher weight
            weightedPriceSum += (price * weight);
            totalWeight += weight;
        }

        double avgPrice = totalPrice / listings.size();
        double avgPricePerSqFt = sqFtCount > 0 ? totalPricePerSqFt / sqFtCount : 0.0;
        double weightedTrendScore = totalWeight > 0 ? weightedPriceSum / totalWeight : avgPrice;

        trends.put("averagePrice", avgPrice);
        trends.put("averagePricePerSqFt", avgPricePerSqFt);
        trends.put("weightedMarketTrendScore", weightedTrendScore);
        trends.put("totalComparablesAnalyzed", listings.size());

        return trends;
    }
}