package com.harshul.incident_intelligence.entity;

import com.harshul.incident_intelligence.domain.enums.IncidentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "incident_status_history")
public class IncidentStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private IncidentStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private IncidentStatus newStatus;

    private LocalDateTime changedAt;

    private String changedBy;

    @ManyToOne
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;
}