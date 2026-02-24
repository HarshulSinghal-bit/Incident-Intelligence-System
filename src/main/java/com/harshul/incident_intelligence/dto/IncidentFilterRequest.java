package com.harshul.incident_intelligence.dto;

import com.harshul.incident_intelligence.domain.enums.EnvironmentType;
import com.harshul.incident_intelligence.domain.enums.IncidentStatus;
import com.harshul.incident_intelligence.domain.enums.SeverityLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncidentFilterRequest {
    private IncidentStatus status;
    private EnvironmentType environment;
    private SeverityLevel severity;
    private String serviceName;
    private String title;
}
