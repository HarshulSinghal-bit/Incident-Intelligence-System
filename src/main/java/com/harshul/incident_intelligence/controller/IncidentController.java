package com.harshul.incident_intelligence.controller;
import com.harshul.incident_intelligence.entity.Incident;
import com.harshul.incident_intelligence.service.IncidentService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.harshul.incident_intelligence.dto.IncidentRequestDTO;
import com.harshul.incident_intelligence.dto.IncidentResponseDTO;
import com.harshul.incident_intelligence.mapper.IncidentMapper;
import java.util.List;

@RestController
@RequestMapping("/incidents")
public class IncidentController {
    private final IncidentService incidentService;
    public IncidentController(IncidentService incidentService){
        this.incidentService = incidentService;
    }

    @GetMapping("/{id}")
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
}
