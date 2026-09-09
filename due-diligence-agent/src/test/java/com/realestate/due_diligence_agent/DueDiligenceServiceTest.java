package com.realestate.due_diligence_agent;

import com.realestate.due_diligence_agent.dto.DueDiligenceResponse;
import com.realestate.due_diligence_agent.entity.*;
import com.realestate.due_diligence_agent.repository.PropertyRepository;
import com.realestate.due_diligence_agent.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DueDiligenceServiceTest {
    @Mock PropertyDataService propertyDataService;
    @Mock FloodZoneService floodZoneService;
    @Mock PermitService permitService;
    @Mock EnvironmentalRecordService environmentalRecordService;
    @Mock UtilityService utilityService;
    @Mock PropertyRepository propertyRepository;
    @InjectMocks DueDiligenceService service;

    @Test
    void returnsAllSevenSuccessfulSections() {
        stubPropertyData();
        DueDiligenceResponse response = service.aggregate(1L);
        assertEquals("SUCCESS", response.ownership().status());
        assertEquals("SUCCESS", response.taxHistory().status());
        assertEquals("SUCCESS", response.zoning().status());
        assertEquals("SUCCESS", response.floodZone().status());
        assertEquals("SUCCESS", response.permits().status());
        assertEquals("SUCCESS", response.environmental().status());
        assertEquals("SUCCESS", response.utilities().status());
    }

    @Test
    void keepsOtherSectionsWhenEnvironmentalServiceFails() {
        stubPropertyData();
        when(environmentalRecordService.getOrFetch(1L)).thenThrow(new RuntimeException("EPA unavailable"));
        DueDiligenceResponse response = service.aggregate(1L);
        assertEquals("FAILED", response.environmental().status());
        assertEquals("EPA unavailable", response.environmental().error());
        assertEquals("SUCCESS", response.ownership().status());
        assertEquals("SUCCESS", response.taxHistory().status());
        assertEquals("SUCCESS", response.zoning().status());
        assertEquals("SUCCESS", response.floodZone().status());
        assertEquals("SUCCESS", response.permits().status());
        assertEquals("SUCCESS", response.utilities().status());
    }

    private void stubPropertyData() {
        when(propertyRepository.existsById(1L)).thenReturn(true);
        when(propertyDataService.ownership(1L)).thenReturn(List.of());
        when(propertyDataService.taxHistory(1L)).thenReturn(List.of());
        when(propertyDataService.zoning(1L)).thenReturn(null);
        when(floodZoneService.getOrFetch(1L)).thenReturn(FloodZoneData.builder().id(1L).build());
        when(permitService.getOrFetch(1L)).thenReturn(List.of());
        when(environmentalRecordService.getOrFetch(1L)).thenReturn(List.of());
        when(utilityService.list(1L)).thenReturn(List.of());
    }
}
