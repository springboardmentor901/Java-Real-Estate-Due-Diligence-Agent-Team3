package com.realestate.due_diligence_agent.repository;

import com.realestate.due_diligence_agent.entity.Permit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PermitRepository extends JpaRepository<Permit, Long> {
    List<Permit> findByPropertyIdOrderByIssueDateAsc(Long propertyId);
}
