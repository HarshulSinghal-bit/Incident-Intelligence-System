package com.harshul.incident_intelligence.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResolutionStatsResponseDTO {

    private long totalIncidents;
    private long totalResolved;
    private long totalOpen;
    private double resolutionRate;
    private double averageResolutionTimeMinutes;
}