package com.harshul.incident_intelligence.domain.specification;

import com.harshul.incident_intelligence.dto.IncidentFilterRequest;
import com.harshul.incident_intelligence.entity.Incident;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class IncidentSpecification {
    public static Specification<Incident> filterBy(IncidentFilterRequest filter) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("status"), filter.getStatus())
                );
            }

            if (filter.getEnvironment() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("environment"), filter.getEnvironment())
                );
            }

            if (filter.getSeverity() != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("finalSeverity"), filter.getSeverity())
                );
            }

            if (filter.getServiceName() != null && !filter.getServiceName().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("serviceName")),
                                "%" + filter.getServiceName().toLowerCase() + "%"
                        )
                );
            }

            if (filter.getTitle() != null && !filter.getTitle().isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("title")),
                                "%" + filter.getTitle().toLowerCase() + "%"
                        )
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
