package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    private Long userId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReportStatus status = ReportStatus.REQUESTED;

    @Column(columnDefinition = "TEXT")
    private String executiveSummary;
    
    // NEW FIELD ADDED FOR MENTOR REQUIREMENT
    @Column(columnDefinition = "TEXT")
    private String propertyTimeline;

    private Double overallRiskScore;

    private String pdfUrl;
    private String excelUrl;

    @Builder.Default
    private LocalDateTime requestedAt = LocalDateTime.now();
    private LocalDateTime completedAt;
}