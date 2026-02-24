package com.harshul.incident_intelligence.service;

import com.harshul.incident_intelligence.domain.enums.IncidentStatus;
import com.harshul.incident_intelligence.domain.enums.SeverityLevel;
import com.harshul.incident_intelligence.domain.intelligence.SeverityCalculator;
import com.harshul.incident_intelligence.domain.lifecycle.IncidentStateMachine;
import com.harshul.incident_intelligence.dto.IncidentFilterRequest;
import com.harshul.incident_intelligence.dto.IncidentRequestDTO;
import com.harshul.incident_intelligence.dto.IncidentResponseDTO;
import com.harshul.incident_intelligence.entity.Incident;
import com.harshul.incident_intelligence.entity.IncidentStatusHistory;
import com.harshul.incident_intelligence.exception.BusinessException;
import com.harshul.incident_intelligence.repository.IncidentRepository;
import com.harshul.incident_intelligence.repository.IncidentStatusHistoryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.harshul.incident_intelligence.domain.fingerprint.FingerprintGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.harshul.incident_intelligence.domain.specification.IncidentSpecification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import com.harshul.incident_intelligence.dto.IncidentStatsResponseDTO;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class IncidentService {
    @Getter
    private final IncidentRepository incidentRepository;
    private final SeverityCalculator severityCalculator;
    //private final IncidentStatusHistoryRepository historyRepository;
    private final IncidentStatusHistoryRepository incidentStatusHistoryRepository;
    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    private SeverityLevel mapScoreToSeverity(double score) {

        if (score >= 0.8) return SeverityLevel.CRITICAL;
        if (score >= 0.6) return SeverityLevel.HIGH;
        if (score >= 0.4) return SeverityLevel.MEDIUM;
        return SeverityLevel.LOW;
    }

    private IncidentResponseDTO mapToResponseDTO(Incident incident) {

        IncidentResponseDTO response = new IncidentResponseDTO();

        response.setFinalSeverity(incident.getFinalSeverity());
        response.setAiSeverityScore(incident.getAiSeverityScore());
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
        response.setConfidenceScore(incident.getConfidenceScore());
        return response;
    }


    public IncidentStatsResponseDTO getIncidentStatistics() {

        IncidentStatsResponseDTO stats = new IncidentStatsResponseDTO();

        // Total count
        stats.setTotalIncidents(incidentRepository.count());

        // Status aggregation
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] row : incidentRepository.countByStatus()) {
            statusMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.setByStatus(statusMap);

        // Severity aggregation
        Map<String, Long> severityMap = new HashMap<>();
        for (Object[] row : incidentRepository.countBySeverity()) {
            if (row[0] != null) {
                severityMap.put(row[0].toString(), (Long) row[1]);
            }
        }
        stats.setBySeverity(severityMap);

        // Environment aggregation
        Map<String, Long> environmentMap = new HashMap<>();
        for (Object[] row : incidentRepository.countByEnvironment()) {
            if (row[0] != null) {
                environmentMap.put(row[0].toString(), (Long) row[1]);
            }
        }
        stats.setByEnvironment(environmentMap);

        return stats;
    }

    public Map<String, Long> getIncidentTrend(int days) {

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);

        List<Object[]> results = incidentRepository.countIncidentsPerDaySince(startDate);

        Map<String, Long> trendMap = new LinkedHashMap<>();

        // Initialize all days with 0
        for (int i = days; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            trendMap.put(date.toString(), 0L);
        }

        // Fill actual values from DB
        for (Object[] row : results) {
            java.sql.Date sqlDate = (java.sql.Date) row[0];
            LocalDate date = sqlDate.toLocalDate();
            Long count = (Long) row[1];
            trendMap.put(date.toString(), count);
        }

        return trendMap;
    }

    public IncidentResponseDTO createIncident(IncidentRequestDTO request) {

        LocalDateTime now = LocalDateTime.now();

        String fingerprint = FingerprintGenerator.generate(request);

        return incidentRepository.findByFingerprint(fingerprint)
                .map(existing -> {

                    existing.setOccurrenceCount(existing.getOccurrenceCount() + 1);
                    existing.setLastSeenAt(now);

                    double score = severityCalculator.calculateScore(existing);
                    SeverityLevel level = mapScoreToSeverity(score);

                    existing.setAiSeverityScore(score);
                    existing.setFinalSeverity(level);
                    existing.setConfidenceScore(0.75);

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

                    double score = severityCalculator.calculateScore(incident);
                    SeverityLevel level = mapScoreToSeverity(score);

                    incident.setAiSeverityScore(score);
                    incident.setFinalSeverity(level);
                    incident.setConfidenceScore(0.75);

                    return mapToResponseDTO(incidentRepository.save(incident));
                });
    }

    public Page<IncidentResponseDTO> searchIncidents(
            IncidentFilterRequest filter,
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Incident> incidentPage = incidentRepository.findAll(
                IncidentSpecification.filterBy(filter),
                pageable
        );

        return incidentPage.map(this::mapToResponseDTO);
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

    @Transactional
    public IncidentResponseDTO changeStatus(Long id, IncidentStatus newStatus) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Incident not found"));

        IncidentStatus oldStatus = incident.getStatus();

        // Validate transition
        IncidentStateMachine.validateTransition(oldStatus, newStatus);

        // Update incident status
        incident.setStatus(newStatus);
        incident.setUpdatedAt(LocalDateTime.now());

        if (newStatus == IncidentStatus.RESOLVED) {
            incident.setResolvedAt(LocalDateTime.now());
        }

        if (newStatus == IncidentStatus.REOPENED) {
            incident.setResolvedAt(null);
        }


        Incident savedIncident = incidentRepository.save(incident);

        //Create history record
        IncidentStatusHistory history = new IncidentStatusHistory();
        history.setIncident(savedIncident);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedAt(LocalDateTime.now());
        history.setChangedBy("SYSTEM"); // temporary, later from auth

        incidentStatusHistoryRepository.save(history);

        return mapToResponseDTO(savedIncident);
    }
}