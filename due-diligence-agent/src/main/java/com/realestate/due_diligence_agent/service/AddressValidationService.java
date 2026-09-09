package com.realestate.due_diligence_agent.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.realestate.due_diligence_agent.dto.AddressValidationRequest;
import com.realestate.due_diligence_agent.dto.AddressValidationResponse;
import com.realestate.due_diligence_agent.entity.Property;
import com.realestate.due_diligence_agent.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.util.retry.Retry;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AddressValidationService {
    private final WebClient webClient;
    private final PropertyRepository propertyRepository;

    @Value("${google.geocoding.base-url:https://maps.googleapis.com/maps/api/geocode/json}")
    private String geocodingUrl;
    @Value("${google.geocoding.api-key:}")
    private String apiKey;

    public AddressValidationResponse validate(AddressValidationRequest request) {
        String address = request.address() == null ? "" : request.address().trim();
        if (address.length() < 5 || address.chars().noneMatch(Character::isLetterOrDigit)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A meaningful address is required");
        }
        if (apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Google geocoding is not configured");
        }
        JsonNode root;
        try {
            root = webClient.get().uri(UriComponentsBuilder.fromUriString(geocodingUrl)
                    .queryParam("address", address).queryParam("key", apiKey).build().toUri())
                    .retrieve().bodyToMono(JsonNode.class)
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(250))
                            .filter(error -> error instanceof WebClientRequestException))
                    .block();
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Google geocoding request failed", ex);
        }
        JsonNode result = root == null ? null : root.path("results").path(0);
        if (result == null || result.isMissingNode() || !"OK".equals(root.path("status").asText())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Address could not be validated");
        }
        String formatted = result.path("formatted_address").asText();
        JsonNode location = result.path("geometry").path("location");
        double latitude = location.path("lat").asDouble();
        double longitude = location.path("lng").asDouble();
        if (formatted.isBlank() || location.path("lat").isMissingNode() || location.path("lng").isMissingNode()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Google returned an invalid address response");
        }
        if (request.propertyId() != null) {
            Property property = propertyRepository.findById(request.propertyId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
            property.setAddress(formatted);
            property.setLatitude(latitude);
            property.setLongitude(longitude);
            propertyRepository.save(property);
        }
        return new AddressValidationResponse(formatted, latitude, longitude);
    }
}
