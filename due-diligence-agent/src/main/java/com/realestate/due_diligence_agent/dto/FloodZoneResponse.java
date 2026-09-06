package com.realestate.due_diligence_agent.dto;

import com.realestate.due_diligence_agent.entity.FloodZoneData;
import java.math.BigDecimal;

public record FloodZoneResponse(Long id, String floodZone, String zoneDescription, String firmPanel,
                                BigDecimal elevation, String source) {
    public static FloodZoneResponse from(FloodZoneData data) {
        return new FloodZoneResponse(data.getId(), data.getFloodZone(), data.getZoneDescription(),
                data.getFirmPanel(), data.getElevation(), data.getSource());
    }
}
