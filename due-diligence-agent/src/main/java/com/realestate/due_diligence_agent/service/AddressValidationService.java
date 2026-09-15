package com.realestate.due_diligence_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realestate.due_diligence_agent.dto.AddressValidationRequest;
import com.realestate.due_diligence_agent.dto.AddressValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressValidationService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${google.api.key:YOUR_GOOGLE_API_KEY}")
    private String googleApiKey;

    public AddressValidationResponse validateAddress(AddressValidationRequest request) {
        String inputAddress = request.getAddress();

        
        if (inputAddress == null || inputAddress.trim().length() < 5) {
            return AddressValidationResponse.builder()
                    .valid(false)
                    .originalAddress(inputAddress)
                    .statusMessage("Invalid address: Address is too short or empty.")
                    .build();
        }

        
        if (googleApiKey.contains("YOUR_GOOGLE") || googleApiKey.isBlank()) {
            log.warn("Using placeholder Google API Key. Returning simulated geocoded coordinates.");
            return AddressValidationResponse.builder()
                    .valid(true)
                    .originalAddress(inputAddress)
                    .formattedAddress(inputAddress.trim() + ", Verified")
                    .latitude(37.7749)
                    .longitude(-122.4194)
                    .statusMessage("Simulated Geocoding: Add a valid Google API key in application.properties for live lookups.")
                    .build();
        }

        try {
           
            String responseBody = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("maps.googleapis.com")
                            .path("/maps/api/geocode/json")
                            .queryParam("address", inputAddress)
                            .queryParam("key", googleApiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.backoff(3, Duration.ofMillis(1000))
                            .doBeforeRetry(retrySignal -> log.warn("Retrying Google Geocoding API call due to failure: {}", retrySignal.failure().getMessage())))
                    .block(Duration.ofSeconds(5));

            JsonNode rootNode = objectMapper.readTree(responseBody);
            String status = rootNode.path("status").asText();

            if ("OK".equalsIgnoreCase(status) && rootNode.path("results").size() > 0) {
                JsonNode firstResult = rootNode.path("results").get(0);
                String formattedAddress = firstResult.path("formatted_address").asText();
                JsonNode location = firstResult.path("geometry").path("location");
                double lat = location.path("lat").asDouble();
                double lng = location.path("lng").asDouble();

                return AddressValidationResponse.builder()
                        .valid(true)
                        .originalAddress(inputAddress)
                        .formattedAddress(formattedAddress)
                        .latitude(lat)
                        .longitude(lng)
                        .statusMessage("Address successfully verified and geocoded.")
                        .build();
            } else {
                return AddressValidationResponse.builder()
                        .valid(false)
                        .originalAddress(inputAddress)
                        .statusMessage("Google API could not verify address: " + status)
                        .build();
            }

        } catch (Exception e) {
            log.error("Google Geocoding API call failed after retries: {}", e.getMessage());
            return AddressValidationResponse.builder()
                    .valid(false)
                    .originalAddress(inputAddress)
                    .statusMessage("Failed to reach Geocoding Service: " + e.getMessage())
                    .build();
        }
    }
}