package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RiskAssessment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "report_id")
    private Report report;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private RiskCategory category;
    private String indicator;
    @Column(precision = 5, scale = 2, nullable = false) private BigDecimal score;
    @Column(columnDefinition = "TEXT") private String notes;
}
