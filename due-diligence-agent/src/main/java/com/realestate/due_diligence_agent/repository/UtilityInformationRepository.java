package com.realestate.due_diligence_agent.repository;

import com.realestate.due_diligence_agent.entity.UtilityInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UtilityInformationRepository extends JpaRepository<UtilityInformation, Long> {
    List<UtilityInformation> findByPropertyIdOrderByUtilityTypeAsc(Long propertyId);
}
