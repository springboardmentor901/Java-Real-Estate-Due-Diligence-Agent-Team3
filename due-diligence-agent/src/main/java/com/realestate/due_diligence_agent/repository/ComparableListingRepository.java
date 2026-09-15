package com.realestate.due_diligence_agent.repository;

import com.realestate.due_diligence_agent.entity.ComparableListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComparableListingRepository extends JpaRepository<ComparableListing, Long> {
    
    // Checks whether comparable listings are already available in the database
    List<ComparableListing> findByPropertyId(Long propertyId);
}