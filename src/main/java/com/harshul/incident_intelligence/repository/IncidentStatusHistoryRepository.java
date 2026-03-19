package com.harshul.incident_intelligence.repository;

import com.harshul.incident_intelligence.entity.IncidentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentStatusHistoryRepository extends JpaRepository<IncidentStatusHistory, Long> {

    List<IncidentStatusHistory> findByIncidentIdOrderByChangedAtAsc(Long incidentId);

}