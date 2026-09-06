package com.realestate.due_diligence_agent.controller;

import com.realestate.due_diligence_agent.dto.ReportResponse;
import com.realestate.due_diligence_agent.entity.User;
import com.realestate.due_diligence_agent.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/api/properties/{id}/reports")
    public ResponseEntity<ReportResponse> generate(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.toResponse(reportService.generate(id, user)));
    }

    @PostMapping("/api/properties/{id}/risk-assessment")
    public ResponseEntity<ReportResponse> assess(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reportService.toResponse(reportService.generate(id, user)));
    }

    @GetMapping("/api/reports/{id}")
    public ReportResponse get(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return reportService.toResponse(reportService.getOwned(id, user));
    }

    @GetMapping("/api/reports")
    public List<ReportResponse> history(@RequestParam Long user, @AuthenticationPrincipal User authenticatedUser) {
        return reportService.history(user, authenticatedUser).stream().map(reportService::toResponse).toList();
    }
}
