package com.harshul.incident_intelligence.repository;
import com.harshul.incident_intelligence.domain.enums.IncidentStatus;
import com.harshul.incident_intelligence.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long>, JpaSpecificationExecutor<Incident> {
    @Query("SELECT i.status, COUNT(i) FROM Incident i GROUP BY i.status")
    List<Object[]> countByStatus();

    @Query("SELECT i.finalSeverity, COUNT(i) FROM Incident i GROUP BY i.finalSeverity")
    List<Object[]> countBySeverity();

    @Query("SELECT i.environment, COUNT(i) FROM Incident i GROUP BY i.environment")
    List<Object[]> countByEnvironment();

    @Query("""
    SELECT DATE(i.createdAt), COUNT(i)
    FROM Incident i
    WHERE i.createdAt >= :startDate
    GROUP BY DATE(i.createdAt)
    ORDER BY DATE(i.createdAt)
""")
    List<Object[]> countIncidentsPerDaySince(@Param("startDate") LocalDateTime startDate);

    long countByStatus(IncidentStatus status);

    @Query(
            value = "SELECT AVG(EXTRACT(EPOCH FROM (resolved_at - created_at)) / 60) " +
                    "FROM incidents WHERE status = 'RESOLVED'",
            nativeQuery = true
    )
    Double averageResolutionTimeInMinutes();

    Optional<Incident> findByFingerprint(String fingerprint);
}
