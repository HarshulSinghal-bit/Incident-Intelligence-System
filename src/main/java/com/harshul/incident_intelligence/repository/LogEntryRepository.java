package com.harshul.incident_intelligence.repository;
import com.harshul.incident_intelligence.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long>{
}