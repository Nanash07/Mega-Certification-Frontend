package com.bankmega.certification.specification;

import com.bankmega.certification.entity.EmployeeEligibility;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class EmployeeEligibilitySpecification {

    public static Specification<EmployeeEligibility> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<EmployeeEligibility> byJobIds(List<Long> jobIds) {
        return (root, query, cb) -> (jobIds == null || jobIds.isEmpty())
                ? cb.conjunction()
                : root.get("employee").get("jobPosition").get("id").in(jobIds);
    }

    public static Specification<EmployeeEligibility> byCertCodes(List<String> certCodes) {
        return (root, query, cb) -> (certCodes == null || certCodes.isEmpty())
                ? cb.conjunction()
                : root.get("certificationRule").get("certification").get("code").in(certCodes);
    }
    
    public static Specification<EmployeeEligibility> byLevels(List<Integer> levels) {
        return (root, query, cb) -> (levels == null || levels.isEmpty())
                ? cb.conjunction()
                : root.get("certificationRule").get("certificationLevel").get("level").in(levels);
    }

    public static Specification<EmployeeEligibility> bySubCodes(List<String> subCodes) {
        return (root, query, cb) -> (subCodes == null || subCodes.isEmpty())
                ? cb.conjunction()
                : root.get("certificationRule").get("subField").get("code").in(subCodes);
    }

    public static Specification<EmployeeEligibility> byStatuses(List<String> statuses) {
        return (root, query, cb) -> (statuses == null || statuses.isEmpty())
                ? cb.conjunction()
                : root.get("status").as(String.class).in(statuses);
    }

    public static Specification<EmployeeEligibility> bySearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("employee").get("nip")), likePattern),
                    cb.like(cb.lower(root.get("employee").get("name")), likePattern),
                    cb.like(cb.lower(root.get("employee").get("jobPosition").get("name")), likePattern),
                    cb.like(cb.lower(root.get("certificationRule").get("certification").get("code")), likePattern),
                    cb.like(cb.lower(root.get("certificationRule").get("certification").get("name")), likePattern),
                    cb.like(cb.lower(root.get("certificationRule").get("subField").get("name")), likePattern)
            );
        };
    }
}