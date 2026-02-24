package com.harshul.incident_intelligence.domain.lifecycle;

import com.harshul.incident_intelligence.domain.enums.IncidentStatus;
import com.harshul.incident_intelligence.exception.BusinessException;

import java.util.Map;
import java.util.Set;

public class IncidentStateMachine {

    private static final Map<IncidentStatus, Set<IncidentStatus>> ALLOWED_TRANSITIONS = Map.of(
            IncidentStatus.CREATED, Set.of(IncidentStatus.ACKNOWLEDGED, IncidentStatus.IN_PROGRESS, IncidentStatus.IGNORED),
            IncidentStatus.ACKNOWLEDGED, Set.of(IncidentStatus.IN_PROGRESS, IncidentStatus.RESOLVED, IncidentStatus.IGNORED),
            IncidentStatus.IN_PROGRESS, Set.of(IncidentStatus.RESOLVED, IncidentStatus.IGNORED),
            IncidentStatus.RESOLVED, Set.of(IncidentStatus.REOPENED),
            IncidentStatus.IGNORED, Set.of(IncidentStatus.REOPENED),
            IncidentStatus.REOPENED, Set.of(IncidentStatus.IN_PROGRESS)
    );

    public static void validateTransition(IncidentStatus current, IncidentStatus target) {

        if (current == target) {
            return; // no-op allowed
        }

        Set<IncidentStatus> allowedTargets = ALLOWED_TRANSITIONS.get(current);

        if (allowedTargets == null || !allowedTargets.contains(target)) {
            throw new BusinessException(
                    "Invalid status transition from " + current + " to " + target
            );
        }
    }
}