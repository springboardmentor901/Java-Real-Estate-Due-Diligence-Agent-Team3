package com.realestate.due_diligence_agent.dto;
import java.time.LocalDate;
public record TimelineEntry(LocalDate date, String label, String description) {}
