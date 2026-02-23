package com.harshul.incident_intelligence.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "log_entry")
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    private String logLevel;

    @Column(length = 4000)
    private String message;

    private LocalDateTime logTimestamp;

    public LogEntry() {}
    public LogEntry(String logLevel, String message) {
        this.logLevel = logLevel;
        this.message = message;
        this.logTimestamp = LocalDateTime.now();
    }

    // Getters and setters
    public void setIncident(Incident incident) {
        this.incident = incident;
    }
    public Incident getIncident() {
        return incident;
    }

}