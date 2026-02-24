package com.harshul.incident_intelligence.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class IncidentStatsResponseDTO {

    private long totalIncidents;

    private Map<String, Long> byStatus;
    private Map<String, Long> bySeverity;
    private Map<String, Long> byEnvironment;
}