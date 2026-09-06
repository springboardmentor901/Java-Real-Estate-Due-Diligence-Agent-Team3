package com.realestate.due_diligence_agent.dto;
import com.realestate.due_diligence_agent.entity.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public record ReportResponse(Long id, Long propertyId, String propertyAddress, Long requestedBy, ReportStatus status,
                             LocalDateTime createdAt, BigDecimal riskScore, String executiveSummary,
                             List<TimelineEntry> propertyTimeline, List<RiskAssessmentResponse> riskAssessments) {}
