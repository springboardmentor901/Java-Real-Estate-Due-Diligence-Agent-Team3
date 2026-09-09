package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "property_id")
    private Property property;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "requested_by")
    private User requestedBy;
    @Enumerated(EnumType.STRING) @Column(nullable = false) @Builder.Default
    private ReportStatus status = ReportStatus.REQUESTED;
    @Column(updatable = false) private LocalDateTime createdAt;
    @Column(precision = 5, scale = 2) private BigDecimal riskScore;
    @Column(columnDefinition = "TEXT") private String executiveSummary;
    @Column(columnDefinition = "TEXT") private String propertyTimeline;
    @Column(columnDefinition = "TEXT") private String failureReason;
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC") @Builder.Default private List<RiskAssessment> riskAssessments = new ArrayList<>();
    @PrePersist void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }
}
