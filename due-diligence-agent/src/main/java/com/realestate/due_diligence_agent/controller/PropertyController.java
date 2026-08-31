package com.realestate.due_diligence_agent.controller;

import com.realestate.due_diligence_agent.dto.NominatimResponse;
import com.realestate.due_diligence_agent.entity.Property;
import com.realestate.due_diligence_agent.integration.client.PropertyApiClient;
import com.realestate.due_diligence_agent.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final PropertyApiClient propertyApiClient;

    @GetMapping("/search")
    public ResponseEntity<List<Property>> searchProperty(
            @RequestParam String address) {

        List<Property> properties =
                propertyService.searchByAddress(address);

        return ResponseEntity.ok(properties);
    }

    @GetMapping("/external-search")
    public ResponseEntity<NominatimResponse> searchExternalProperty(
            @RequestParam String address) {

        NominatimResponse result =
                propertyApiClient.validateAddress(address);

        return ResponseEntity.ok(result);
    }
}