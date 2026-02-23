package com.harshul.incident_intelligence.repository;

import com.harshul.incident_intelligence.entity.IncidentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentStatusHistoryRepository extends JpaRepository<IncidentStatusHistory, Long> {
}
