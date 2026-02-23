package com.harshul.incident_intelligence.repository;
import com.harshul.incident_intelligence.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long>{
    Optional<Incident> findByFingerprint(String fingerprint);
}
