package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.RiskAssessmentResult;
import com.realestate.due_diligence_agent.entity.*;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class OwnershipVerificationStrategy implements RiskCategoryStrategy {
    public RiskAssessmentResult assess(Property p) {
        int score = p.getOwnershipRecords().isEmpty() ? 50 : 0;
        boolean joint = p.getOwnershipRecords().stream().anyMatch(o -> o.getOwnershipType() != null &&
                (o.getOwnershipType().toLowerCase().contains("joint") || o.getOwnershipType().toLowerCase().contains("multiple")));
        if (joint) score += 35;
        LocalDate recent = LocalDate.now().minusYears(1);
        if (p.getOwnershipRecords().stream().anyMatch(o -> o.getAcquisitionDate() != null && o.getAcquisitionDate().isAfter(recent))) score += 25;
        score = Math.min(score, 100);
        return new RiskAssessmentResult(RiskCategory.OWNERSHIP_VERIFICATION,
                p.getOwnershipRecords().isEmpty() ? "Ownership data unavailable" : joint ? "Joint or multiple ownership recorded" : "Ownership record available",
                BigDecimal.valueOf(score), "Recent acquisitions and joint ownership require additional verification.");
    }
}
