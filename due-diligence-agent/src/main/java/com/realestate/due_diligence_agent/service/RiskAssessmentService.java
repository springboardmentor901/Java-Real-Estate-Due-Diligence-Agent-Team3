package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.RiskAssessmentResult;
import com.realestate.due_diligence_agent.entity.*;
import com.realestate.due_diligence_agent.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RiskAssessmentService {
    private final ReportRepository reportRepository;
    private final RiskAssessmentRepository assessmentRepository;
    private final TaxDueAnalysisStrategy tax;
    private final FloodRiskStrategy flood;
    private final ZoningComplianceStrategy zoning;
    private final PermitComplianceStrategy permit;
    private final OwnershipVerificationStrategy ownership;
    private final LegalRiskStrategy legal;

    @Transactional
    public List<RiskAssessmentResult> assess(Property property, User user, Report report) {
        List<RiskAssessmentResult> results = List.of(tax.assess(property), flood.assess(property), zoning.assess(property),
                permit.assess(property), ownership.assess(property), legal.assess(property));
        report.getRiskAssessments().clear();
        results.forEach(r -> report.getRiskAssessments().add(RiskAssessment.builder().report(report)
                .category(r.category()).indicator(r.indicator()).score(r.score()).notes(r.notes()).build()));
        BigDecimal average = results.stream().map(RiskAssessmentResult::score).reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(results.size()), 2, RoundingMode.HALF_UP);
        report.setRiskScore(average);
        reportRepository.save(report);
        return results;
    }
}
