package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.RiskAssessmentResult;
import com.realestate.due_diligence_agent.entity.*;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class LegalRiskStrategy implements RiskCategoryStrategy {
    public RiskAssessmentResult assess(Property property) {
        return new RiskAssessmentResult(RiskCategory.LEGAL_RISK,
                "Legal document data is currently unavailable; legal risk could not be fully assessed.",
                BigDecimal.ZERO, "No legal document data source is present in the current application.");
    }
}
