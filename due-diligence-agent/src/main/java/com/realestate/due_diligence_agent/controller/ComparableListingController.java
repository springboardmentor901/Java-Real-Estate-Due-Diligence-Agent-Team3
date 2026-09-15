package com.realestate.due_diligence_agent.controller;

import com.realestate.due_diligence_agent.entity.ComparableListing;
import com.realestate.due_diligence_agent.service.ComparableListingService;
import com.realestate.due_diligence_agent.service.MarketTrendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class ComparableListingController {

    private final ComparableListingService comparableListingService;
    private final MarketTrendService marketTrendService;

    @GetMapping("/{id}/comparables")
    public ResponseEntity<List<ComparableListing>> getComparables(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "distance") String sortBy) {
        
        List<ComparableListing> listings = comparableListingService.getOrFetchComparables(id);

        // Support sorting by Distance, Price, or Listed Date
        if ("price".equalsIgnoreCase(sortBy)) {
            listings.sort(Comparator.comparing(ComparableListing::getPrice, Comparator.nullsLast(Comparator.naturalOrder())));
        } else if ("date".equalsIgnoreCase(sortBy)) {
            listings.sort(Comparator.comparing(ComparableListing::getListedDate, Comparator.nullsLast(Comparator.reverseOrder())));
        } else {
            listings.sort(Comparator.comparing(ComparableListing::getDistanceMiles, Comparator.nullsLast(Comparator.naturalOrder())));
        }

        return ResponseEntity.ok(listings);
    }

    @GetMapping("/{id}/comparables/trends")
    public ResponseEntity<Map<String, Object>> getMarketTrends(@PathVariable Long id) {
        // Ensures comparables are loaded/cached first, then calculates trends
        comparableListingService.getOrFetchComparables(id);
        Map<String, Object> trends = marketTrendService.calculateTrends(id);
        return ResponseEntity.ok(trends);
    }
}