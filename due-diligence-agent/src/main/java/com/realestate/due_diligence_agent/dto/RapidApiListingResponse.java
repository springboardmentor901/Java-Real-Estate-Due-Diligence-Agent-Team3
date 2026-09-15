package com.realestate.due_diligence_agent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RapidApiListingResponse {

    @JsonProperty("results")
    private List<ListingItem> results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ListingItem {
        @JsonProperty("address")
        private String address;

        @JsonProperty("price")
        private Double price;

        @JsonProperty("latitude")
        private Double latitude;

        @JsonProperty("longitude")
        private Double longitude;

        @JsonProperty("listDate")
        private String listDate; // Will parse to LocalDate in service

        @JsonProperty("squareFootage")
        private Double squareFootage;

        @JsonProperty("source")
        private String source;
    }
}