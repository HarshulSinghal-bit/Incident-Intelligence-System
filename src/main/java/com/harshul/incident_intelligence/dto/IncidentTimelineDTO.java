package com.harshul.incident_intelligence.dto;

import com.harshul.incident_intelligence.domain.enums.IncidentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class IncidentTimelineDTO {

    private IncidentStatus oldStatus;
    private IncidentStatus newStatus;
    private LocalDateTime changedAt;
}