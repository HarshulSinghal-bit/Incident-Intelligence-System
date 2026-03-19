package com.harshul.incident_intelligence.integration;

import com.harshul.incident_intelligence.domain.enums.EnvironmentType;
import com.harshul.incident_intelligence.domain.enums.SourceSystem;
import com.harshul.incident_intelligence.dto.IncidentRequestDTO;
import com.harshul.incident_intelligence.dto.IncidentResponseDTO;
import com.harshul.incident_intelligence.service.IncidentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/integrations/jenkins")
public class JenkinsController {

    private final IncidentService incidentService;

    public JenkinsController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    public IncidentResponseDTO handleJenkins(@RequestBody Map<String, Object> payload) {

        IncidentRequestDTO request = new IncidentRequestDTO();

        request.setTitle("Jenkins Build Failure");
        request.setServiceName((String) payload.getOrDefault("jobName", "unknown-service"));
        request.setEnvironment(EnvironmentType.DEV);
        request.setSourceSystem(SourceSystem.JENKINS);
        request.setLogSnippet((String) payload.getOrDefault("error", "Build failed"));
        System.out.println("Received Jenkins event: " + payload);
        return incidentService.createIncident(request);
    }
}