package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.entity.OwnershipRecord;
import com.realestate.due_diligence_agent.entity.PropertyHistory;
import com.realestate.due_diligence_agent.entity.TaxHistory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PropertyTimelineBuilder {

    // Helper class to unify different event types for chronological sorting
    private static class TimelineEvent implements Comparable<TimelineEvent> {
        String dateStr;
        String label;
        String description;

        public TimelineEvent(String dateStr, String label, String description) {
            this.dateStr = dateStr != null ? dateStr : "Unknown Date";
            this.label = label;
            this.description = description;
        }

        @Override
        public int compareTo(TimelineEvent other) {
            return this.dateStr.compareTo(other.dateStr); // Basic string sort (YYYY-MM-DD formats well)
        }
    }

    public String buildTimeline(List<OwnershipRecord> ownerships, List<TaxHistory> taxes, List<PropertyHistory> histories) {
        List<TimelineEvent> events = new ArrayList<>();

        // 1. Process Ownership Data
        if (ownerships != null) {
            for (OwnershipRecord owner : ownerships) {
                events.add(new TimelineEvent(
                    owner.getPurchaseDate() != null ? owner.getPurchaseDate().toString() : "Unknown",
                    "OWNERSHIP TRANSFER",
                    "Title transferred to " + owner.getPrimaryOwnerName() + " via " + owner.getDeedType()
                ));
            }
        }

        // 2. Process Tax Data
        if (taxes != null) {
            for (TaxHistory tax : taxes) {
                events.add(new TimelineEvent(
                    String.valueOf(tax.getTaxYear()),
                    "TAX ASSESSMENT",
                    "Assessed value: $" + tax.getTotalAssessedValue() + " | Status: " + tax.getTaxStatus()
                ));
            }
        }

        // 3. Process Permit / Event Data
        if (histories != null) {
            for (PropertyHistory hist : histories) {
                events.add(new TimelineEvent(
                    hist.getEventDate() != null ? hist.getEventDate().toString() : "Unknown",
                    hist.getEventType(),
                    hist.getDescription()
                ));
            }
        }

        // Sort chronologically
        Collections.sort(events);

        // Build the final string
        StringBuilder timelineBuilder = new StringBuilder();
        for (TimelineEvent event : events) {
            timelineBuilder.append(String.format("[%s] %s: %s\n", event.dateStr, event.label, event.description));
        }

        return timelineBuilder.length() > 0 ? timelineBuilder.toString() : "No timeline events recorded.";
    }
}