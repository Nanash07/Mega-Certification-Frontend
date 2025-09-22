package com.bankmega.certification.specification;

import com.bankmega.certification.entity.EmployeeEligibilityException;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class EmployeeEligibilityExceptionSpecification {

    public static Specification<EmployeeEligibilityException> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<EmployeeEligibilityException> byJobIds(List<Long> jobIds) {
        return (root, query, cb) -> (jobIds == null || jobIds.isEmpty())
                ? cb.conjunction()
                : root.get("employee").get("jobPosition").get("id").in(jobIds);
    }

    public static Specification<EmployeeEligibilityException> byCertCodes(List<String> certCodes) {
        return (root, query, cb) -> (certCodes == null || certCodes.isEmpty())
                ? cb.conjunction()
                : root.get("certificationRule").get("certification").get("code").in(certCodes);
    }

    public static Specification<EmployeeEligibilityException> byLevels(List<Integer> levels) {
        return (root, query, cb) -> (levels == null || levels.isEmpty())
                ? cb.conjunction()
                : root.get("certificationRule").get("certificationLevel").get("level").in(levels);
    }

    public static Specification<EmployeeEligibilityException> bySubCodes(List<String> subCodes) {
        return (root, query, cb) -> (subCodes == null || subCodes.isEmpty())
                ? cb.conjunction()
                : root.get("certificationRule").get("subField").get("code").in(subCodes);
    }
    
    public static Specification<EmployeeEligibilityException> byStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank()) {
                return cb.conjunction();
            }

            String normalized = status.trim().toUpperCase();
            if ("AKTIF".equals(normalized)) {
                return cb.isTrue(root.get("isActive"));
            } else if ("NONAKTIF".equals(normalized)) {
                return cb.isFalse(root.get("isActive"));
            }

            return cb.conjunction();
        };
    }

    public static Specification<EmployeeEligibilityException> bySearch(String keyword) {
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