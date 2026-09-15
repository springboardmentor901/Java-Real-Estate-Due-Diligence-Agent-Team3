package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.entity.*;
import com.realestate.due_diligence_agent.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final PropertyRepository propertyRepository;
    private final PdfReportGenerator pdfReportGenerator;
    private final ExcelReportGenerator excelReportGenerator;
    private final FileStorageService fileStorageService;
    
    // NEW MENTOR INJECTIONS
    private final ExecutiveSummaryGenerator executiveSummaryGenerator;
    private final PropertyTimelineBuilder propertyTimelineBuilder;
    private final OwnershipRecordRepository ownershipRecordRepository;
    private final TaxHistoryRepository taxHistoryRepository;
    private final PropertyHistoryRepository propertyHistoryRepository;

    public Report requestReport(Long propertyId, Long userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found: " + propertyId));

        Report report = Report.builder()
                .property(property)
                .userId(userId)
                .status(ReportStatus.REQUESTED)
                .build();
        report = reportRepository.save(report);

        processReport(report.getId());
        return report;
    }

    public void processReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportId));

        report.setStatus(ReportStatus.IN_PROGRESS);
        reportRepository.save(report);

        try {
            Property property = report.getProperty();
            Long propId = property.getId();

            // 1. Gather Existing Data for Timeline
         // 1. Gather Existing Data for Timeline
            List<OwnershipRecord> ownerships = ownershipRecordRepository.findByPropertyIdOrderByPurchaseDateDesc(propId);
            List<TaxHistory> taxes = taxHistoryRepository.findByPropertyIdOrderByTaxYearDesc(propId);
            List<PropertyHistory> events = propertyHistoryRepository.findByPropertyIdOrderByEventDateDesc(propId);

            // 2. Build Timeline
            String timeline = propertyTimelineBuilder.buildTimeline(ownerships, taxes, events);
            report.setPropertyTimeline(timeline);

            // 3. Risk Assessment (Placeholder logic for now)
            double overallRiskScore = 22.0; 
            int comparableCount = 0; // Will be updated in the next module

            // 4. Generate Summary
            String summary = executiveSummaryGenerator.generateSummary(property, overallRiskScore, comparableCount);
            report.setExecutiveSummary(summary);

            // 5. Generate Files
            byte[] pdfBytes = pdfReportGenerator.generatePdf(property, summary, overallRiskScore);
            String pdfFileName = "report_" + report.getId() + ".pdf";
            fileStorageService.saveFile(pdfFileName, pdfBytes);

            byte[] excelBytes = excelReportGenerator.generateExcel(property);
            String excelFileName = "report_" + report.getId() + ".xlsx";
            fileStorageService.saveFile(excelFileName, excelBytes);

            // 6. Save final COMPLETED state
            report.setStatus(ReportStatus.COMPLETED);
            report.setOverallRiskScore(overallRiskScore);
            report.setPdfUrl(pdfFileName);
            report.setExcelUrl(excelFileName);
            report.setCompletedAt(LocalDateTime.now());
            
            reportRepository.save(report);

        } catch (Exception e) {
            log.error("Failed to generate report: {}", e.getMessage());
            report.setStatus(ReportStatus.FAILED);
            report.setExecutiveSummary("Report generation failed due to internal error: " + e.getMessage());
            reportRepository.save(report);
        }
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with ID: " + id));
    }

    public List<Report> getUserReports(Long userId) {
        return reportRepository.findByUserIdOrderByRequestedAtDesc(userId);
    }

    public byte[] downloadPdf(Long reportId) throws IOException {
        Report report = getReportById(reportId);
        return fileStorageService.loadFile(report.getPdfUrl());
    }

    public byte[] downloadExcel(Long reportId) throws IOException {
        Report report = getReportById(reportId);
        return fileStorageService.loadFile(report.getExcelUrl());
    }
}