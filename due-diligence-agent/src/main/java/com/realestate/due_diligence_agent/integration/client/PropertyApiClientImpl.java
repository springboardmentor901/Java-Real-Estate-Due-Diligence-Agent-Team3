package com.realestate.due_diligence_agent.integration.client;

import com.realestate.due_diligence_agent.dto.NominatimResponse;
import com.realestate.due_diligence_agent.integration.exception.ExternalApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PropertyApiClientImpl implements PropertyApiClient {

    private final RestClient restClient;

    public PropertyApiClientImpl(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader("User-Agent", "RealEstateDueDiligenceAgent/1.0")
                .build();
    }

    @Override
    public NominatimResponse validateAddress(String address) {

        try {
            NominatimResponse[] response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", address)
                            .queryParam("format", "json")
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .body(NominatimResponse[].class);

            if (response == null || response.length == 0) {
                throw new ExternalApiException(
                        "No valid address found for: " + address
                );
            }

            return response[0];

        } catch (ExternalApiException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new ExternalApiException(
                    "Failed to validate address using external API",
                    ex
            );
        }
    }
}