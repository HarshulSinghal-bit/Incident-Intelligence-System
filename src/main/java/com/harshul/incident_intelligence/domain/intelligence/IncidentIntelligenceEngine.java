package com.harshul.incident_intelligence.domain.intelligence;

public interface IncidentIntelligenceEngine {

    IntelligenceResult analyze(String logSnippet);
}