package com.realestate.due_diligence_agent.repository;

import com.realestate.due_diligence_agent.entity.EnvironmentalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EnvironmentalRecordRepository extends JpaRepository<EnvironmentalRecord, Long> {
    List<EnvironmentalRecord> findByPropertyIdOrderByIdAsc(Long propertyId);
}
