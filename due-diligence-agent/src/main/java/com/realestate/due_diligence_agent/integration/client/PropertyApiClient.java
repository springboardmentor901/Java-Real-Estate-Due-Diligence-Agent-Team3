package com.realestate.due_diligence_agent.integration.client;

import com.realestate.due_diligence_agent.dto.NominatimResponse;

public interface PropertyApiClient {

    NominatimResponse validateAddress(String address);
}