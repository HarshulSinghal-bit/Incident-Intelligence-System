package com.harshul.incident_intelligence.dto;

import com.harshul.incident_intelligence.domain.enums.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class IncidentResponseDTO {

    private Long id;
    private String title;
    private SourceSystem sourceSystem;
    private EnvironmentType environment;
    private String serviceName;
    private IncidentStatus status;
    private SeverityLevel finalSeverity;
    private Integer occurrenceCount;
    private LocalDateTime createdAt;
    private LocalDateTime firstSeenAt;
    private LocalDateTime lastSeenAt;
    // Getters and Setters

}