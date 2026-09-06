package com.realestate.due_diligence_agent.dto;

import com.realestate.due_diligence_agent.entity.RiskCategory;
import java.math.BigDecimal;

public record RiskAssessmentResult(RiskCategory category, String indicator, BigDecimal score, String notes) {}
