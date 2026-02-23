package com.harshul.incident_intelligence.dto;

import com.harshul.incident_intelligence.domain.enums.EnvironmentType;
import com.harshul.incident_intelligence.domain.enums.SourceSystem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class IncidentRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @NotNull(message = "Source system is required")
    private SourceSystem sourceSystem;

    @NotNull(message = "Environment is required")
    private EnvironmentType environment;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    private String failureType;
    private String errorCode;
    private String rootCauseClass;
    private String logSnippet;

    // Getters and Setters

}