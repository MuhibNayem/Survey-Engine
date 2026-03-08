package com.bracits.surveyengine.response.repository;

import com.bracits.surveyengine.response.entity.ResponseStatus;
import com.bracits.surveyengine.response.entity.SurveyResponse;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SurveyResponseSpecification {

    public static Specification<SurveyResponse> matchesFilters(
            UUID campaignId,
            String tenantId,
            Map<String, String> metadataFilters) {
        
        return (Root<SurveyResponse> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter by campaign and tenant
            predicates.add(cb.equal(root.get("campaignId"), campaignId));
            predicates.add(cb.equal(root.get("tenantId"), tenantId));

            // Add metadata filters if present
            if (metadataFilters != null && !metadataFilters.isEmpty()) {
                metadataFilters.forEach((key, value) -> {
                    if (value == null || value.isBlank()) {
                        return;
                    }
                    Predicate jsonPredicate = cb.equal(
                            cb.function("jsonb_extract_path_text", String.class, root.get("respondentMetadata"), cb.literal(key)),
                            value
                    );
                    predicates.add(jsonPredicate);
                });
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<SurveyResponse> matchesFiltersAndStatus(
            UUID campaignId,
            String tenantId,
            ResponseStatus status,
            Map<String, String> metadataFilters) {
        
        return (Root<SurveyResponse> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate base = matchesFilters(campaignId, tenantId, metadataFilters).toPredicate(root, query, cb);
            return cb.and(base, cb.equal(root.get("status"), status));
        };
    }
}
