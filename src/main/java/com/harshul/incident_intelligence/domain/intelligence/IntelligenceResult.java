package com.harshul.incident_intelligence.domain.intelligence;

import com.harshul.incident_intelligence.domain.enums.SeverityLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IntelligenceResult {

    private final SeverityLevel severity;
    private final double score;
    private final double confidence;
    private final String detectedFailureType;
    private final String detectedRootCause;
}