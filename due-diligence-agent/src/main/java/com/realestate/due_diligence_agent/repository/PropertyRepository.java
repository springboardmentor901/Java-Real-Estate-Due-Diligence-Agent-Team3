package com.realestate.due_diligence_agent.repository;

import com.realestate.due_diligence_agent.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    // Search by address keyword ignoring case
    List<Property> findByAddressContainingIgnoreCase(String address);

    // Search by city
    List<Property> findByCityIgnoreCase(String city);
}