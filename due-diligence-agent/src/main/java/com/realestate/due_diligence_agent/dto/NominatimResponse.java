package com.realestate.due_diligence_agent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NominatimResponse {

    @JsonProperty("display_name")
    private String displayName;

    private String lat;
    private String lon;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}