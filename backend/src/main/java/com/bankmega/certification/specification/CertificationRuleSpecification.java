package com.bankmega.certification.specification;

import com.bankmega.certification.entity.CertificationRule;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class CertificationRuleSpecification {

    public static Specification<CertificationRule> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<CertificationRule> byCertIds(List<Long> certIds) {
        return (root, query, cb) -> (certIds == null || certIds.isEmpty())
                ? cb.conjunction()
                : root.get("certification").get("id").in(certIds);
    }

    public static Specification<CertificationRule> byLevelIds(List<Long> levelIds) {
        return (root, query, cb) -> (levelIds == null || levelIds.isEmpty())
                ? cb.conjunction()
                : root.get("certificationLevel").get("id").in(levelIds);
    }

    public static Specification<CertificationRule> bySubIds(List<Long> subIds) {
        return (root, query, cb) -> (subIds == null || subIds.isEmpty())
                ? cb.conjunction()
                : root.get("subField").get("id").in(subIds);
    }

    public static Specification<CertificationRule> byStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.equalsIgnoreCase("all")) {
                return cb.conjunction();
            }
            if (status.equalsIgnoreCase("active")) {
                return cb.isTrue(root.get("isActive"));
            }
            if (status.equalsIgnoreCase("inactive")) {
                return cb.isFalse(root.get("isActive"));
            }
            return cb.conjunction();
        };
    }

    public static Specification<CertificationRule> bySearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("certification").get("name")), likePattern),
                    cb.like(cb.lower(root.get("certification").get("code")), likePattern),
                    cb.like(cb.lower(root.get("subField").get("name")), likePattern),
                    cb.like(cb.lower(root.get("subField").get("code")), likePattern)
            );
        };
    }
}