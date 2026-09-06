package com.realestate.due_diligence_agent.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaxHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "property_id")
    private Property property;
    private LocalDate taxDate;
    private LocalDate dueDate;
    @Column(precision = 14, scale = 2) private BigDecimal amount;
    @Builder.Default private boolean paid = true;
}
