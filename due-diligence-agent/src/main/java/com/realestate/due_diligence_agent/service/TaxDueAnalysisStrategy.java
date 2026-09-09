package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.RiskAssessmentResult;
import com.realestate.due_diligence_agent.entity.*;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class TaxDueAnalysisStrategy implements RiskCategoryStrategy {
    public RiskAssessmentResult assess(Property property) {
        BigDecimal overdue = property.getTaxHistory().stream()
                .filter(t -> !t.isPaid())
                .map(t -> t.getAmount() == null ? BigDecimal.ZERO : t.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal score = overdue.signum() == 0 ? BigDecimal.ZERO :
                overdue.compareTo(new BigDecimal("1000")) <= 0 ? new BigDecimal("40") :
                overdue.compareTo(new BigDecimal("10000")) <= 0 ? new BigDecimal("70") : new BigDecimal("100");
        String indicator = overdue.signum() == 0 ? "No unpaid or overdue taxes found" :
                "Unpaid taxes total " + overdue.toPlainString();
        return new RiskAssessmentResult(RiskCategory.TAX_DUE, indicator, score,
                "Tax risk is based on the total amount marked unpaid in tax history.");
    }
}
