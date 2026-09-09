package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.*;
import com.realestate.due_diligence_agent.entity.*;
import com.realestate.due_diligence_agent.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final PropertyRepository propertyRepository;
    private final RiskAssessmentService riskAssessmentService;
    private final PropertyTimelineBuilder timelineBuilder;
    private final ExecutiveSummaryGenerator summaryGenerator;

    public Report generate(Long propertyId, User user) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
        Report report = reportRepository.findFirstByPropertyIdAndRequestedByIdAndStatusInOrderByCreatedAtDesc(
                propertyId, user.getId(), List.of(ReportStatus.REQUESTED, ReportStatus.IN_PROGRESS, ReportStatus.COMPLETED))
                .orElseGet(() -> reportRepository.save(Report.builder().property(property).requestedBy(user).build()));
        report.setStatus(ReportStatus.IN_PROGRESS);
        reportRepository.save(report);
        try {
            List<RiskAssessmentResult> results = riskAssessmentService.assess(property, user, report);
            List<TimelineEntry> timeline = timelineBuilder.build(property);
            report.setPropertyTimeline(writeTimeline(timeline));
            report.setExecutiveSummary(summaryGenerator.generate(property, report.getRiskScore(), results));
            report.setStatus(ReportStatus.COMPLETED);
            return reportRepository.save(report);
        } catch (RuntimeException ex) {
            report.setStatus(ReportStatus.FAILED);
            report.setFailureReason(ex.getMessage());
            reportRepository.save(report);
            throw ex;
        }
    }

    private String writeTimeline(List<TimelineEntry> timeline) {
        return timeline.stream()
                .map(x -> x.date() + "\t" + clean(x.label()) + "\t" + clean(x.description()))
                .collect(Collectors.joining("\n"));
    }
    private String clean(String value) { return value == null ? "" : value.replace("\t", " ").replace("\n", " "); }

    @Transactional(readOnly = true)
    public Report getOwned(Long id, User user) {
        Report r = reportRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
        if (!r.getRequestedBy().getId().equals(user.getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Report access denied");
        return r;
    }
    @Transactional(readOnly = true)
    public List<Report> history(Long userId, User user) {
        if (!user.getId().equals(userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Report history access denied");
        return reportRepository.findByRequestedByIdOrderByCreatedAtDesc(userId);
    }
    public ReportResponse toResponse(Report r) {
        List<TimelineEntry> timeline = readTimeline(r.getPropertyTimeline());
        List<RiskAssessmentResponse> risks = r.getRiskAssessments().stream()
                .map(x -> new RiskAssessmentResponse(x.getId(), x.getCategory(), x.getIndicator(), x.getScore(), x.getNotes())).toList();
        return new ReportResponse(r.getId(), r.getProperty().getId(), r.getProperty().getAddress(), r.getRequestedBy().getId(),
                r.getStatus(), r.getCreatedAt(), r.getRiskScore(), r.getExecutiveSummary(), timeline, risks);
    }
    private List<TimelineEntry> readTimeline(String value) {
        if (value == null || value.isBlank()) return List.of();
        return Arrays.stream(value.split("\\R"))
                .map(line -> line.split("\t", 3))
                .filter(parts -> parts.length == 3)
                .map(parts -> new TimelineEntry(java.time.LocalDate.parse(parts[0]), parts[1], parts[2]))
                .toList();
    }
}
