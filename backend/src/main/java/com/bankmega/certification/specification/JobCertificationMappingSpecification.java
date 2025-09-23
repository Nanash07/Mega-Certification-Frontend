package com.bankmega.certification.specification;

import com.bankmega.certification.entity.JobCertificationMapping;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class JobCertificationMappingSpecification {

    public static Specification<JobCertificationMapping> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<JobCertificationMapping> byEmployeeIds(List<Long> employeeIds) {
        return (root, query, cb) ->
                (employeeIds == null || employeeIds.isEmpty())
                        ? cb.conjunction()
                        : root.get("employee").get("id").in(employeeIds);
    }

    public static Specification<JobCertificationMapping> byJobIds(List<Long> jobIds) {
        return (root, query, cb) -> (jobIds == null || jobIds.isEmpty())
                ? cb.conjunction()
                : root.get("jobPosition").get("id").in(jobIds);
    }

    public static Specification<JobCertificationMapping> byCertCodes(List<String> certCodes) {
        return (root, query, cb) -> (certCodes == null || certCodes.isEmpty())
                ? cb.conjunction()
                : root.get("certificationRule").get("certification").get("code").in(certCodes);
    }

    public static Specification<JobCertificationMapping> byLevels(List<Integer> levels) {
        return (root, query, cb) -> (levels == null || levels.isEmpty())
                ? cb.conjunction()
                : root.get("certificationRule").get("certificationLevel").get("level").in(levels);
    }

    public static Specification<JobCertificationMapping> bySubCodes(List<String> subCodes) {
        return (root, query, cb) -> (subCodes == null || subCodes.isEmpty())
                ? cb.conjunction()
                : root.get("certificationRule").get("subField").get("code").in(subCodes);
    }

    public static Specification<JobCertificationMapping> byStatus(String status) {
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

    public static Specification<JobCertificationMapping> bySearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("jobPosition").get("name")), likePattern),
                    cb.like(cb.lower(root.get("certificationRule").get("certification").get("code")), likePattern),
                    cb.like(cb.lower(root.get("certificationRule").get("certification").get("name")), likePattern)
            );
        };
    }
}