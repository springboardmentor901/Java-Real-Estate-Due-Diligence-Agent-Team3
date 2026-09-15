package com.realestate.due_diligence_agent.repository;

import com.realestate.due_diligence_agent.entity.ZoningInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZoningInformationRepository extends JpaRepository<ZoningInformation, Long> {
    Optional<ZoningInformation> findByPropertyId(Long propertyId);
}