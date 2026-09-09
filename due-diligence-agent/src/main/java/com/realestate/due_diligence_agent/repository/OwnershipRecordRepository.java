package com.realestate.due_diligence_agent.repository;

import com.realestate.due_diligence_agent.entity.OwnershipRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OwnershipRecordRepository extends JpaRepository<OwnershipRecord, Long> {
    List<OwnershipRecord> findByPropertyIdOrderByAcquisitionDateAsc(Long propertyId);
}
