package com.harshul.incident_intelligence.domain.intelligence;

import com.harshul.incident_intelligence.entity.Incident;

public interface SeverityCalculator {

    double calculateScore(Incident incident);

}