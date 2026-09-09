package com.realestate.due_diligence_agent.repository;

import com.realestate.due_diligence_agent.entity.FloodZoneData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FloodZoneDataRepository extends JpaRepository<FloodZoneData, Long> {
    Optional<FloodZoneData> findByPropertyId(Long propertyId);
}
