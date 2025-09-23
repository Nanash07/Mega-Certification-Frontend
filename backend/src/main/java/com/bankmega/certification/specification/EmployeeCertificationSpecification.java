package com.bankmega.certification.specification;

import com.bankmega.certification.entity.EmployeeCertification;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class EmployeeCertificationSpecification {

    public static Specification<EmployeeCertification> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<EmployeeCertification> byEmployeeIds(List<Long> ids) {
        return (root, query, cb) ->
                (ids == null || ids.isEmpty())
                        ? cb.conjunction()
                        : root.get("employee").get("id").in(ids);
    }

    public static Specification<EmployeeCertification> byRuleIds(List<Long> ids) {
        return (root, query, cb) ->
                (ids == null || ids.isEmpty())
                        ? cb.conjunction()
                        : root.get("certificationRule").get("id").in(ids);
    }

    public static Specification<EmployeeCertification> byInstitutionIds(List<Long> ids) {
        return (root, query, cb) ->
                (ids == null || ids.isEmpty())
                        ? cb.conjunction()
                        : root.get("institution").get("id").in(ids);
    }

    public static Specification<EmployeeCertification> byStatuses(List<String> statuses) {
        return (root, query, cb) ->
                (statuses == null || statuses.isEmpty())
                        ? cb.conjunction()
                        : root.get("status").as(String.class).in(statuses);
    }

    // 🔹 Filter by Certification Code
    public static Specification<EmployeeCertification> byCertCodes(List<String> codes) {
        return (root, query, cb) ->
                (codes == null || codes.isEmpty())
                        ? cb.conjunction()
                        : root.get("certificationRule").get("certification").get("code").in(codes);
    }

    // 🔹 Filter by Certification Level
    public static Specification<EmployeeCertification> byLevels(List<Integer> levels) {
        return (root, query, cb) ->
                (levels == null || levels.isEmpty())
                        ? cb.conjunction()
                        : root.get("certificationRule").get("certificationLevel").get("level").in(levels);
    }

    // 🔹 Filter by SubField Code
    public static Specification<EmployeeCertification> bySubCodes(List<String> codes) {
        return (root, query, cb) ->
                (codes == null || codes.isEmpty())
                        ? cb.conjunction()
                        : root.get("certificationRule").get("subField").get("code").in(codes);
    }

    public static Specification<EmployeeCertification> byCertDateRange(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("certDate"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("certDate"), start);
            } else if (end != null) {
                return cb.lessThanOrEqualTo(root.get("certDate"), end);
            }
            return cb.conjunction();
        };
    }

    public static Specification<EmployeeCertification> byValidUntilRange(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("validUntil"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("validUntil"), start);
            } else if (end != null) {
                return cb.lessThanOrEqualTo(root.get("validUntil"), end);
            }
            return cb.conjunction();
        };
    }


    public static Specification<EmployeeCertification> bySearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }
            String like = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("certNumber")), like),
                    cb.like(cb.lower(root.get("employee").get("nip")), like),
                    cb.like(cb.lower(root.get("employee").get("name")), like),
                    cb.like(cb.lower(root.get("certificationRule").get("certification").get("name")), like),
                    cb.like(cb.lower(root.get("certificationRule").get("certification").get("code")), like),
                    cb.like(cb.lower(root.get("institution").get("name")), like)
            );
        };
    }
}