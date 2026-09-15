package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.entity.Property;
import org.springframework.stereotype.Component;

@Component
public class ExecutiveSummaryGenerator {

    public String generateSummary(Property property, double overallRiskScore, int comparableCount) {
        String riskLevel = overallRiskScore < 30 ? "LOW" : overallRiskScore < 70 ? "MODERATE" : "HIGH";
        
        return String.format(
            "Executive Summary for Property at %s, %s:\n\n" +
            "1. Risk Assessment: The overall composite risk score is %.2f/100, indicating a %s level of risk. " +
            "This encompasses legal, financial, and environmental evaluations.\n\n" +
            "2. Comparable Market Analysis: A total of %d comparable listings were found nearby, forming the basis of our market trend assessment.\n\n" +
            "3. Conclusion: Based on public records, deed transfers, and available permit histories, the property " +
            "has passed initial automated due diligence checks. Please refer to the timeline and individual risk modules for detailed findings.",
            property.getAddress(), property.getCity(), overallRiskScore, riskLevel, comparableCount
        );
    }
}