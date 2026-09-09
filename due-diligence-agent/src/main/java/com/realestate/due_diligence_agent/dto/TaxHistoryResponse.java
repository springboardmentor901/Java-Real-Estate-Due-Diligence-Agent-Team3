package com.realestate.due_diligence_agent.dto;

import com.realestate.due_diligence_agent.entity.TaxHistory;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TaxHistoryResponse(Long id, LocalDate taxDate, LocalDate dueDate, BigDecimal amount, boolean paid) {
    public static TaxHistoryResponse from(TaxHistory record) {
        return new TaxHistoryResponse(record.getId(), record.getTaxDate(), record.getDueDate(), record.getAmount(), record.isPaid());
    }
}
