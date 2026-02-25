package com.harshul.incident_intelligence.domain.intelligence;

import com.harshul.incident_intelligence.domain.enums.SeverityLevel;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedIncidentIntelligenceEngine implements IncidentIntelligenceEngine {

    @Override
    public IntelligenceResult analyze(String logSnippet) {

        if (logSnippet == null || logSnippet.isBlank()) {
            return new IntelligenceResult(
                    SeverityLevel.LOW,
                    0.1,
                    0.5,
                    "UNKNOWN",
                    "No log provided"
            );
        }

        String log = logSnippet.toLowerCase();

        if (log.contains("outofmemoryerror")) {
            return new IntelligenceResult(
                    SeverityLevel.CRITICAL,
                    0.95,
                    0.9,
                    "MEMORY_FAILURE",
                    "Out of memory detected"
            );
        }

        if (log.contains("nullpointerexception")) {
            return new IntelligenceResult(
                    SeverityLevel.HIGH,
                    0.75,
                    0.8,
                    "NULL_POINTER",
                    "Null reference access"
            );
        }

        if (log.contains("timeout") || log.contains("connection refused")) {
            return new IntelligenceResult(
                    SeverityLevel.MEDIUM,
                    0.6,
                    0.75,
                    "NETWORK_FAILURE",
                    "Connectivity issue"
            );
        }

        return new IntelligenceResult(
                SeverityLevel.LOW,
                0.3,
                0.6,
                "GENERAL_ERROR",
                "Unclassified issue"
        );
    }
}