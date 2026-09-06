package com.realestate.due_diligence_agent.dto;

public record DueDiligenceResponse(Long propertyId, DueDiligenceSection ownership, DueDiligenceSection taxHistory,
                                   DueDiligenceSection zoning, DueDiligenceSection floodZone,
                                   DueDiligenceSection permits, DueDiligenceSection environmental,
                                   DueDiligenceSection utilities) {}
