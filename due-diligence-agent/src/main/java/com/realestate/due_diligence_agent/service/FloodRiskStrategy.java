package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.RiskAssessmentResult;
import com.realestate.due_diligence_agent.entity.*;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class FloodRiskStrategy implements RiskCategoryStrategy {
    public RiskAssessmentResult assess(Property p) {
        String value = (p.getFloodRiskRating() + " " + p.getFloodZone()).toLowerCase();
        BigDecimal score = value.contains("high") || value.contains("a") ? new BigDecimal("80") :
                value.contains("medium") || value.contains("moderate") || value.contains("b") ? new BigDecimal("50") :
                value.isBlank() || value.contains("null") ? new BigDecimal("30") : BigDecimal.TEN;
        String indicator = p.getFloodRiskRating() == null && p.getFloodZone() == null
                ? "Flood data unavailable" : "Flood rating: " + String.valueOf(p.getFloodRiskRating());
        return new RiskAssessmentResult(RiskCategory.FLOOD_RISK, indicator, score,
                "Flood score uses the property's flood risk rating and zone.");
    }
}
