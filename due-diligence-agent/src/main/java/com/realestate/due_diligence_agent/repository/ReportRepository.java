package com.realestate.due_diligence_agent.repository;

import com.realestate.due_diligence_agent.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUserIdOrderByRequestedAtDesc(Long userId);
    List<Report> findByPropertyIdOrderByRequestedAtDesc(Long propertyId);
}