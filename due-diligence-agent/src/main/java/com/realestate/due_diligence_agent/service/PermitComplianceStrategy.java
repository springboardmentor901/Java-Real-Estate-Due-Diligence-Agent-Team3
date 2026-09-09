package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.RiskAssessmentResult;
import com.realestate.due_diligence_agent.entity.*;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class PermitComplianceStrategy implements RiskCategoryStrategy {
    public RiskAssessmentResult assess(Property p) {
        long missing = p.getPermits().stream().filter(x -> x.getStatus() == null || x.getStatus().isBlank()).count();
        long incomplete = p.getPermits().stream().filter(x -> x.getStatus() != null &&
                (x.getStatus().equalsIgnoreCase("incomplete") || x.getStatus().equalsIgnoreCase("expired")
                        || x.getStatus().equalsIgnoreCase("rejected"))).count();
        BigDecimal score = p.getPermits().isEmpty() ? new BigDecimal("50") :
                incomplete > 0 ? new BigDecimal("80") : missing > 0 ? new BigDecimal("40") : BigDecimal.ZERO;
        return new RiskAssessmentResult(RiskCategory.PERMIT_COMPLIANCE,
                p.getPermits().isEmpty() ? "No permits recorded" : incomplete > 0 ? "Incomplete or invalid permits found" : "Recorded permits appear complete",
                score, "Permit compliance is based on the permit records and their status.");
    }
}
