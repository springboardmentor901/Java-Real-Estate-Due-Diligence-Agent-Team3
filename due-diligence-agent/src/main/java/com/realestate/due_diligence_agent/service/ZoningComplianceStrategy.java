package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.RiskAssessmentResult;
import com.realestate.due_diligence_agent.entity.*;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class ZoningComplianceStrategy implements RiskCategoryStrategy {
    public RiskAssessmentResult assess(Property p) {
        String compliance = p.getZoningCompliance() == null ? "" : p.getZoningCompliance().toLowerCase();
        boolean compliant = compliance.contains("compliant") || compliance.equals("yes") || compliance.equals("approved");
        boolean missing = compliance.isBlank() || p.getLandUse() == null || p.getSetbackRequirements() == null;
        BigDecimal score = compliant && !missing ? BigDecimal.ZERO : missing ? new BigDecimal("50") : new BigDecimal("80");
        return new RiskAssessmentResult(RiskCategory.ZONING_COMPLIANCE,
                compliant && !missing ? "Zoning information indicates compliance" : "Zoning compliance requires review",
                score, "Evaluated zoning compliance, land use, and setback requirements.");
    }
}
