package com.harshul.incident_intelligence.entity;

import com.harshul.incident_intelligence.domain.enums.IncidentStatus;
import com.harshul.incident_intelligence.domain.enums.SeverityLevel;
import com.harshul.incident_intelligence.domain.enums.SourceSystem;
import com.harshul.incident_intelligence.domain.enums.EnvironmentType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "incidents",
        indexes = {
                @Index(name = "idx_fingerprint", columnList = "fingerprint")
        })
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String externalRunId;

    @Enumerated(EnumType.STRING)
    private SourceSystem sourceSystem;

    private String serviceName;

    @Enumerated(EnumType.STRING)
    private EnvironmentType environment;

    private String buildNumber;
    private String branchName;
    private String commitHash;

    private String title;
    private String failureType;
    private String errorCode;
    private String rootCauseClass;

    @Column(length = 2000)
    private String logSnippet;

    @Enumerated(EnumType.STRING)
    private SeverityLevel finalSeverity;

    private Double aiSeverityScore;
    private Double confidenceScore;

    @Enumerated(EnumType.STRING)
    private IncidentStatus status;

    private Integer occurrenceCount;
    private LocalDateTime firstSeenAt;
    private LocalDateTime lastSeenAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(nullable = false, unique = true)
    private String fingerprint;

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogEntry> logs = new ArrayList<>();

    public void addLog(LogEntry logEntry) {
        logs.add(logEntry);
        logEntry.setIncident(this);
    }

    public void removeLog(LogEntry logEntry) {
        logs.remove(logEntry);
        logEntry.setIncident(null);
    }
}