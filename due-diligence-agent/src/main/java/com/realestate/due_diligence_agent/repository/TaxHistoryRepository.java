package com.realestate.due_diligence_agent.repository;

import com.realestate.due_diligence_agent.entity.TaxHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaxHistoryRepository extends JpaRepository<TaxHistory, Long> {
    List<TaxHistory> findByPropertyIdOrderByTaxDateAsc(Long propertyId);
}
