package com.realestate.due_diligence_agent.client;

import com.realestate.due_diligence_agent.dto.RapidApiListingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class RapidApiListingClient {

    private final WebClient webClient;

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    @Value("${rapidapi.host}")
    private String rapidApiHost;

    public RapidApiListingResponse fetchComparables(String city, String state) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/properties/list-by-location") // Adjust endpoint based on your specific RapidAPI subscription
                            .queryParam("city", city)
                            .queryParam("state", state)
                            .build())
                    .header("X-RapidAPI-Key", rapidApiKey)
                    .header("X-RapidAPI-Host", rapidApiHost)
                    .retrieve()
                    .bodyToMono(RapidApiListingResponse.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))) // Retry mechanism
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch listings from RapidAPI: {}", e.getMessage());
            return new RapidApiListingResponse(); // Fallback empty response
        }
    }
}