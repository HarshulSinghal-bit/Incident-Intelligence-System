package com.harshul.incident_intelligence.domain.intelligence;

import com.harshul.incident_intelligence.entity.Incident;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedSeverityCalculator implements SeverityCalculator {

    @Override
    public double calculateScore(Incident incident) {

        double score = 0.0;

        // Environment weight
        if (incident.getEnvironment() != null) {
            switch (incident.getEnvironment()) {
                case PROD -> score += 0.5;
                case STAGING -> score += 0.3;
                case UAT -> score += 0.2;
                case QA -> score += 0.1;
                case DEV -> score += 0.05;
            }
        }

        // Occurrence weight
        if (incident.getOccurrenceCount() != null) {
            score += Math.min(incident.getOccurrenceCount() * 0.05, 0.3);
        }

        // Error presence
        if (incident.getErrorCode() != null) {
            score += 0.1;
        }

        return Math.min(score, 1.0);
    }
}