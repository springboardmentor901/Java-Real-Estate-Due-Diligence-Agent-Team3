package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.TimelineEntry;
import com.realestate.due_diligence_agent.entity.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class PropertyTimelineBuilder {
    public List<TimelineEntry> build(Property p) {
        List<TimelineEntry> result = new ArrayList<>();
        p.getOwnershipRecords().forEach(o -> add(result, o.getAcquisitionDate(), "Ownership acquired",
                "Ownership recorded for " + (o.getOwnerName() == null ? "unknown owner" : o.getOwnerName())));
        p.getTaxHistory().forEach(t -> add(result, t.getTaxDate() != null ? t.getTaxDate() : t.getDueDate(),
                "Tax record", t.isPaid() ? "Tax marked paid" : "Tax marked unpaid"));
        p.getPermits().forEach(x -> add(result, x.getIssueDate(), "Permit issued",
                (x.getPermitType() == null ? "Permit" : x.getPermitType()) + " status: " + String.valueOf(x.getStatus())));
        result.sort(Comparator.comparing(TimelineEntry::date, Comparator.nullsLast(Comparator.naturalOrder())));
        return result;
    }
    private void add(List<TimelineEntry> entries, LocalDate date, String label, String description) {
        if (date != null) entries.add(new TimelineEntry(date, label, description));
    }
}
