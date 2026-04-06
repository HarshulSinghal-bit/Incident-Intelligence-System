package com.harshul.incident_intelligence.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harshul.incident_intelligence.domain.enums.IncidentStatus;
import com.harshul.incident_intelligence.domain.intelligence.IncidentIntelligenceEngine;
import com.harshul.incident_intelligence.domain.intelligence.IntelligenceResult;
import com.harshul.incident_intelligence.domain.lifecycle.IncidentStateMachine;
import com.harshul.incident_intelligence.domain.fingerprint.FingerprintGenerator;
import com.harshul.incident_intelligence.domain.specification.IncidentSpecification;
import com.harshul.incident_intelligence.dto.*;
import com.harshul.incident_intelligence.entity.Incident;
import com.harshul.incident_intelligence.entity.IncidentStatusHistory;
import com.harshul.incident_intelligence.exception.BusinessException;
import com.harshul.incident_intelligence.repository.IncidentRepository;
import com.harshul.incident_intelligence.repository.IncidentStatusHistoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentStatusHistoryRepository incidentStatusHistoryRepository;
    private final IncidentIntelligenceEngine intelligenceEngine;
    private final IncidentAIService incidentAIService;
    public IncidentStatsResponseDTO getIncidentStatistics() {

        IncidentStatsResponseDTO stats = new IncidentStatsResponseDTO();

        stats.setTotalIncidents(incidentRepository.count());

        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] row : incidentRepository.countByStatus()) {
            statusMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.setByStatus(statusMap);

        Map<String, Long> severityMap = new HashMap<>();
        for (Object[] row : incidentRepository.countBySeverity()) {
            if (row[0] != null) {
                severityMap.put(row[0].toString(), (Long) row[1]);
            }
        }
        stats.setBySeverity(severityMap);

        Map<String, Long> environmentMap = new HashMap<>();
        for (Object[] row : incidentRepository.countByEnvironment()) {
            if (row[0] != null) {
                environmentMap.put(row[0].toString(), (Long) row[1]);
            }
        }
        stats.setByEnvironment(environmentMap);

        return stats;
    }
    // ==============================
    // BASIC CRUD SUPPORT
    // ==============================

    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Incident not found"));
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public void deleteIncident(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new BusinessException("Incident not found");
        }
        incidentRepository.deleteById(id);
    }
    // ==============================
    // CREATE INCIDENT pushed already
    // ==============================

    public IncidentResponseDTO createIncident(IncidentRequestDTO request) {

        LocalDateTime now = LocalDateTime.now();
        String fingerprint = FingerprintGenerator.generate(request);

        return incidentRepository.findByFingerprint(fingerprint)
                .map(existing -> {
                    applyAIAnalysis(existing, existing.getLogSnippet());
                    existing.setOccurrenceCount(existing.getOccurrenceCount() + 1);
                    existing.setLastSeenAt(now);

                    IntelligenceResult result =
                            intelligenceEngine.analyze(existing.getLogSnippet());

                    applyIntelligence(existing, result);

                    return mapToResponseDTO(incidentRepository.save(existing));
                })
                .orElseGet(() -> {

                    Incident incident = new Incident();
                    applyAIAnalysis(incident, request.getLogSnippet());
                    incident.setTitle(request.getTitle());
                    incident.setServiceName(request.getServiceName());
                    incident.setEnvironment(request.getEnvironment());
                    incident.setSourceSystem(request.getSourceSystem());
                    incident.setLogSnippet(request.getLogSnippet());

                    incident.setFingerprint(fingerprint);
                    incident.setCreatedAt(now);
                    incident.setFirstSeenAt(now);
                    incident.setLastSeenAt(now);
                    incident.setOccurrenceCount(1);
                    incident.setStatus(IncidentStatus.CREATED);

                    IntelligenceResult result =
                            intelligenceEngine.analyze(request.getLogSnippet());

                    applyIntelligence(incident, result);
                    //String aiResponse = incidentAIService.analyzeLog(request.getLogSnippet());
                    return mapToResponseDTO(incidentRepository.save(incident));


                });
    }

    // ==============================
    // UPDATE INCIDENT complete
    // ==============================

    public Incident updateIncident(Long id, Incident updatedIncident) {

        Incident existing = incidentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Incident not found"));

        existing.setTitle(updatedIncident.getTitle());
        existing.setLogSnippet(updatedIncident.getLogSnippet());
        existing.setUpdatedAt(LocalDateTime.now());

        IntelligenceResult result =
                intelligenceEngine.analyze(existing.getLogSnippet());

        applyIntelligence(existing, result);

        return incidentRepository.save(existing);
    }
    private void applyAIAnalysis(Incident incident, String logSnippet) {
        try {
            String aiResponse = incidentAIService.analyzeLog(logSnippet);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> aiResult = mapper.readValue(aiResponse, Map.class);

            String rootCause = (String) aiResult.get("rootCause");
            Double confidence = ((Number) aiResult.get("confidence")).doubleValue();

            incident.setRootCauseClass(rootCause);
            incident.setAiSeverityScore(confidence);

        } catch (Exception e) {
            System.out.println("AI parsing failed: " + e.getMessage());
        }
    }

    // ==============================
    // STATUS CHANGE
    // ==============================

    @Transactional
    public IncidentResponseDTO changeStatus(Long id, IncidentStatus newStatus) {

        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Incident not found"));

        IncidentStatus oldStatus = incident.getStatus();

        IncidentStateMachine.validateTransition(oldStatus, newStatus);

        incident.setStatus(newStatus);
        incident.setUpdatedAt(LocalDateTime.now());

        if (newStatus == IncidentStatus.RESOLVED) {
            incident.setResolvedAt(LocalDateTime.now());
        }

        if (newStatus == IncidentStatus.REOPENED) {
            incident.setResolvedAt(null);
        }

        Incident saved = incidentRepository.save(incident);

        IncidentStatusHistory history = new IncidentStatusHistory();
        history.setIncident(saved);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedAt(LocalDateTime.now());
        history.setChangedBy("SYSTEM");

        incidentStatusHistoryRepository.save(history);

        return mapToResponseDTO(saved);
    }

    // ==============================
    // SEARCH havent pushed it
    // ==============================

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

        Page<Incident> incidentPage =
                incidentRepository.findAll(
                        IncidentSpecification.filterBy(filter),
                        pageable
                );

        return incidentPage.map(this::mapToResponseDTO);
    }

    // ==============================
    // ANALYTICS havent pushed
    // ==============================

    public ResolutionStatsResponseDTO getResolutionStats() {

        long total = incidentRepository.count();
        long resolved = incidentRepository.countByStatus(IncidentStatus.RESOLVED);
        long open = total - resolved;

        Double avg = incidentRepository.averageResolutionTimeInMinutes();

        double resolutionRate =
                total > 0 ? (double) resolved / total * 100 : 0.0;

        double avgMinutes =
                avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;

        ResolutionStatsResponseDTO dto = new ResolutionStatsResponseDTO();
        dto.setTotalIncidents(total);
        dto.setTotalResolved(resolved);
        dto.setTotalOpen(open);
        dto.setResolutionRate(resolutionRate);
        dto.setAverageResolutionTimeMinutes(avgMinutes);

        return dto;
    }

    public Map<String, Long> getIncidentTrend(int days) {

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Object[]> results =
                incidentRepository.countIncidentsPerDaySince(startDate);

        Map<String, Long> trendMap = new LinkedHashMap<>();

        for (int i = days; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            trendMap.put(date.toString(), 0L);
        }

        for (Object[] row : results) {
            java.sql.Date sqlDate = (java.sql.Date) row[0];
            LocalDate date = sqlDate.toLocalDate();
            Long count = (Long) row[1];
            trendMap.put(date.toString(), count);
        }

        return trendMap;
    }

    // ==============================
    // HELPERS pused it
    // ==============================

    private void applyIntelligence(Incident incident, IntelligenceResult result) {

        incident.setFinalSeverity(result.getSeverity());
        incident.setAiSeverityScore(result.getScore());
        incident.setConfidenceScore(result.getConfidence());
        incident.setFailureType(result.getDetectedFailureType());
        incident.setRootCauseClass(result.getDetectedRootCause());
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
        response.setFinalSeverity(incident.getFinalSeverity());
        response.setAiSeverityScore(incident.getAiSeverityScore());
        response.setConfidenceScore(incident.getConfidenceScore());
        response.setCreatedAt(incident.getCreatedAt());
        response.setFirstSeenAt(incident.getFirstSeenAt());
        response.setLastSeenAt(incident.getLastSeenAt());

        return response;
    }

    public Map<String, Long> getTopFailingServices() {

        List<Object[]> results = incidentRepository.countIncidentsByService();

        Map<String, Long> map = new LinkedHashMap<>();

        for (Object[] row : results) {
            map.put((String) row[0], (Long) row[1]);
        }

        return map;
    }

    public Map<String, Long> getTopFailureTypes() {

        List<Object[]> results = incidentRepository.countByFailureType();

        Map<String, Long> map = new LinkedHashMap<>();

        for (Object[] row : results) {
            map.put((String) row[0], (Long) row[1]);
        }

        return map;
    }

    public List<IncidentTimelineDTO> getIncidentTimeline(Long incidentId) {

        List<IncidentStatusHistory> historyList =
                incidentStatusHistoryRepository.findByIncidentIdOrderByChangedAtAsc(incidentId);

        List<IncidentTimelineDTO> timeline = new ArrayList<>();

        for (IncidentStatusHistory history : historyList) {

            IncidentTimelineDTO dto = new IncidentTimelineDTO();
            dto.setOldStatus(history.getOldStatus());
            dto.setNewStatus(history.getNewStatus());
            dto.setChangedAt(history.getChangedAt());

            timeline.add(dto);
        }

        return timeline;
    }

    public Map<String, String> detectSpikes() {

        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);

        List<Object[]> recent = incidentRepository.countRecentIncidentsByService(last24Hours);
        List<Object[]> historical = incidentRepository.countHistoricalIncidentsByService(last24Hours);

        Map<String, Long> recentMap = new HashMap<>();
        Map<String, Long> historicalMap = new HashMap<>();

        for (Object[] row : recent) {
            recentMap.put((String) row[0], (Long) row[1]);
        }

        for (Object[] row : historical) {
            historicalMap.put((String) row[0], (Long) row[1]);
        }

        Map<String, String> result = new LinkedHashMap<>();

        for (String service : recentMap.keySet()) {

            long recentCount = recentMap.getOrDefault(service, 0L);
            long historicalCount = historicalMap.getOrDefault(service, 0L);

            if (recentCount > historicalCount) {
                result.put(service, "SPIKE");
            } else {
                result.put(service, "NORMAL");
            }
        }

        return result;
    }
}