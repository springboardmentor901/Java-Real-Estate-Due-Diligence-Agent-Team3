package com.realestate.due_diligence_agent.repository;
import com.realestate.due_diligence_agent.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByRequestedByIdOrderByCreatedAtDesc(Long userId);
    Optional<Report> findFirstByPropertyIdAndRequestedByIdAndStatusInOrderByCreatedAtDesc(Long propertyId, Long userId, Collection<ReportStatus> statuses);
}
