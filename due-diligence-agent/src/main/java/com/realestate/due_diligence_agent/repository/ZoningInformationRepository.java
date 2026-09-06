package com.realestate.due_diligence_agent.repository;

import com.realestate.due_diligence_agent.entity.ZoningInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ZoningInformationRepository extends JpaRepository<ZoningInformation, Long> {
    Optional<ZoningInformation> findByPropertyId(Long propertyId);
}
