package com.realestate.due_diligence_agent.service;

import com.realestate.due_diligence_agent.dto.UtilityRequest;
import com.realestate.due_diligence_agent.entity.*;
import com.realestate.due_diligence_agent.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilityService {
    private final PropertyRepository propertyRepository;
    private final UtilityInformationRepository utilityRepository;

    public List<UtilityInformation> list(Long propertyId) {
        requireProperty(propertyId);
        return utilityRepository.findByPropertyIdOrderByUtilityTypeAsc(propertyId);
    }

    @Transactional
    public UtilityInformation create(Long propertyId, UtilityRequest request, User user) {
        Property property = requireProperty(propertyId);
        return utilityRepository.save(UtilityInformation.builder().property(property)
                .utilityType(request.utilityType()).provider(request.provider())
                .accountReference(request.accountReference()).status(request.status())
                .source(user.getUsername()).build());
    }

    @Transactional
    public UtilityInformation update(Long propertyId, Long utilityId, UtilityRequest request) {
        UtilityInformation utility = ownedUtility(propertyId, utilityId);
        utility.setUtilityType(request.utilityType());
        utility.setProvider(request.provider());
        utility.setAccountReference(request.accountReference());
        utility.setStatus(request.status());
        return utilityRepository.save(utility);
    }

    @Transactional
    public void delete(Long propertyId, Long utilityId) {
        utilityRepository.delete(ownedUtility(propertyId, utilityId));
    }

    private Property requireProperty(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
    }
    private UtilityInformation ownedUtility(Long propertyId, Long utilityId) {
        UtilityInformation utility = utilityRepository.findById(utilityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utility information not found"));
        if (!utility.getProperty().getId().equals(propertyId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utility information not found");
        }
        return utility;
    }
}
