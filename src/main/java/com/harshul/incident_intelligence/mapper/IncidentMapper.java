package com.harshul.incident_intelligence.mapper;

import com.harshul.incident_intelligence.dto.IncidentRequestDTO;
import com.harshul.incident_intelligence.dto.IncidentResponseDTO;
import com.harshul.incident_intelligence.entity.Incident;

public class IncidentMapper {

    // Convert Request DTO → Entity
    public static Incident toEntity(IncidentRequestDTO dto) {

        Incident incident = new Incident();

        incident.setTitle(dto.getTitle());
        incident.setSourceSystem(dto.getSourceSystem());
        incident.setEnvironment(dto.getEnvironment());
        incident.setServiceName(dto.getServiceName());
        incident.setFailureType(dto.getFailureType());
        incident.setErrorCode(dto.getErrorCode());
        incident.setRootCauseClass(dto.getRootCauseClass());
        incident.setLogSnippet(dto.getLogSnippet());

        return incident;
    }

    // Convert Entity → Response DTO
    public static IncidentResponseDTO toResponseDTO(Incident incident) {

        IncidentResponseDTO response = new IncidentResponseDTO();

        response.setId(incident.getId());
        response.setTitle(incident.getTitle());
        response.setSourceSystem(incident.getSourceSystem());
        response.setEnvironment(incident.getEnvironment());
        response.setServiceName(incident.getServiceName());
        response.setStatus(incident.getStatus());
        response.setFinalSeverity(incident.getFinalSeverity());
        response.setOccurrenceCount(incident.getOccurrenceCount());
        response.setCreatedAt(incident.getCreatedAt());

        return response;
    }
}