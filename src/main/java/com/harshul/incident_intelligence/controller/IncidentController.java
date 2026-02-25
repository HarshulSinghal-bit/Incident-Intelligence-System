package com.harshul.incident_intelligence.controller;
import com.harshul.incident_intelligence.domain.enums.EnvironmentType;
import com.harshul.incident_intelligence.domain.enums.IncidentStatus;
import com.harshul.incident_intelligence.domain.enums.SeverityLevel;
import com.harshul.incident_intelligence.dto.*;
import com.harshul.incident_intelligence.entity.Incident;
import com.harshul.incident_intelligence.service.IncidentService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.harshul.incident_intelligence.mapper.IncidentMapper;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/incidents")
public class IncidentController {
    private final IncidentService incidentService;
    public IncidentController(IncidentService incidentService){
        this.incidentService = incidentService;
    }

    @GetMapping("/{id:\\d+}")
    public IncidentResponseDTO getIncidentById(@PathVariable Long id) {

        Incident incident = incidentService.getIncidentById(id);

        return IncidentMapper.toResponseDTO(incident);
    }

    @GetMapping
    public List<IncidentResponseDTO> getAllIncidents() {

        return incidentService.getAllIncidents()
                .stream()
                .map(IncidentMapper::toResponseDTO)
                .toList();
    }

    @PutMapping("/{id}")
    public Incident updateIncident(@PathVariable Long id, @RequestBody Incident incident){
        return incidentService.updateIncident(id, incident);
    }

    @DeleteMapping("/{id}")
    public void deleteIncident(@PathVariable Long id){
        incidentService.deleteIncident(id);
    }

    @PostMapping("/test-string")
    public String test(@RequestBody String body) {
        return body;
    }

    @PostMapping
    public IncidentResponseDTO createIncident(
            @Valid @RequestBody IncidentRequestDTO request
    ) {
        return incidentService.createIncident(request);
    }

    @PatchMapping("/{id}/status")
    public IncidentResponseDTO changeStatus(
            @PathVariable Long id,
            @RequestParam IncidentStatus status
    ) {
        return incidentService.changeStatus(id, status);
    }

    @GetMapping("/stats")
    public IncidentStatsResponseDTO getIncidentStats() {
        return incidentService.getIncidentStatistics();
    }

    @GetMapping("/stats/trend")
    public Map<String, Long> getIncidentTrend(
            @RequestParam(defaultValue = "7") int days
    ) {
        return incidentService.getIncidentTrend(days);
    }

    @GetMapping("/stats/resolution")
    public ResolutionStatsResponseDTO getResolutionStats() {
        return incidentService.getResolutionStats();
    }

    @GetMapping("/search")
    public Page<IncidentResponseDTO> searchIncidents(

            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(required = false) EnvironmentType environment,
            @RequestParam(required = false) SeverityLevel severity,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String title,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {

        IncidentFilterRequest filter = new IncidentFilterRequest();
        filter.setStatus(status);
        filter.setEnvironment(environment);
        filter.setSeverity(severity);
        filter.setServiceName(serviceName);
        filter.setTitle(title);

        return incidentService.searchIncidents(filter, page, size, sortBy, direction);
    }
}
