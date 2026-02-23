package com.harshul.incident_intelligence.service;

import com.harshul.incident_intelligence.domain.enums.IncidentStatus;
import com.harshul.incident_intelligence.dto.IncidentRequestDTO;
import com.harshul.incident_intelligence.dto.IncidentResponseDTO;
import com.harshul.incident_intelligence.entity.Incident;
import com.harshul.incident_intelligence.exception.BusinessException;
import com.harshul.incident_intelligence.repository.IncidentRepository;
import com.harshul.incident_intelligence.repository.IncidentStatusHistoryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.harshul.incident_intelligence.domain.fingerprint.FingerprintGenerator;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class IncidentService {
    @Getter
    private final IncidentRepository incidentRepository;
    private final IncidentStatusHistoryRepository historyRepository;

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    private IncidentResponseDTO mapToResponseDTO(Incident incident) {

        IncidentResponseDTO response = new IncidentResponseDTO();

        response.setId(incident.getId());
        response.setTitle(incident.getTitle());
        response.setServiceName(incident.getServiceName());
        response.setEnvironment(incident.getEnvironment());
        response.setSourceSystem(incident.getSourceSystem());
        response.setStatus(incident.getStatus());
        response.setOccurrenceCount(incident.getOccurrenceCount());
        response.setFirstSeenAt(incident.getFirstSeenAt());
        response.setLastSeenAt(incident.getLastSeenAt());
        response.setCreatedAt(incident.getCreatedAt());
        return response;
    }

    public IncidentResponseDTO createIncident(IncidentRequestDTO request) {

        LocalDateTime now = LocalDateTime.now();

        String fingerprint = FingerprintGenerator.generate(request);

        return incidentRepository.findByFingerprint(fingerprint)
                .map(existing -> {
                    existing.setOccurrenceCount(existing.getOccurrenceCount() + 1);
                    existing.setLastSeenAt(now);
                    return mapToResponseDTO(incidentRepository.save(existing));
                })
                .orElseGet(() -> {

                    Incident incident = new Incident();

                    incident.setTitle(request.getTitle());
                    incident.setServiceName(request.getServiceName());
                    incident.setFailureType(request.getFailureType());
                    incident.setRootCauseClass(request.getRootCauseClass());
                    incident.setErrorCode(request.getErrorCode());
                    incident.setEnvironment(request.getEnvironment());
                    incident.setSourceSystem(request.getSourceSystem());

                    incident.setFingerprint(fingerprint);
                    incident.setCreatedAt(now);
                    incident.setFirstSeenAt(now);
                    incident.setLastSeenAt(now);
                    incident.setOccurrenceCount(1);
                    incident.setStatus(IncidentStatus.CREATED);

                    return mapToResponseDTO(incidentRepository.save(incident));
                });
    }

    public Incident updateIncident(Long id, Incident updatedIncident) {

        Incident existing = incidentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Incident not found"));

        existing.setTitle(updatedIncident.getTitle());
        existing.setFailureType(updatedIncident.getFailureType());
        existing.setErrorCode(updatedIncident.getErrorCode());
        existing.setRootCauseClass(updatedIncident.getRootCauseClass());
        existing.setLogSnippet(updatedIncident.getLogSnippet());
        existing.setUpdatedAt(LocalDateTime.now());

        return incidentRepository.save(existing);
    }

    public void deleteIncident(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new BusinessException("Incident not found");
        }
        incidentRepository.deleteById(id);
    }

    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id).orElseThrow(() -> new BusinessException("Incident not found"));
    }
}