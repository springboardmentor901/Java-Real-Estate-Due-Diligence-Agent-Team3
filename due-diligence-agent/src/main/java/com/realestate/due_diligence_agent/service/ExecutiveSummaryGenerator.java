package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.RiskAssessmentResult;
import com.realestate.due_diligence_agent.entity.Property;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExecutiveSummaryGenerator {
    public String generate(Property property, BigDecimal overallScore, List<RiskAssessmentResult> results) {
        String categories = results.stream().map(r -> r.category().name() + " (" + r.score().stripTrailingZeros().toPlainString()
                + "): " + r.indicator()).collect(Collectors.joining("; "));
        return "Property " + property.getAddress() + " has an overall risk score of "
                + overallScore.stripTrailingZeros().toPlainString() + ". Risk categories: " + categories + ".";
    }
}
