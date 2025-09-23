package com.bankmega.certification.specification;

import com.bankmega.certification.entity.EmployeeEligibility;
import com.bankmega.certification.entity.EligibilitySource;
import com.bankmega.certification.entity.EligibilityStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeeEligibilitySpecification {

    public static Specification<EmployeeEligibility> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<EmployeeEligibility> byEmployeeIds(List<Long> employeeIds) {
        return (root, query, cb) ->
                (employeeIds == null || employeeIds.isEmpty())
                        ? cb.conjunction()
                        : root.get("employee").get("id").in(employeeIds);
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
        return (root, query, cb) -> {
            if (statuses == null || statuses.isEmpty()) return cb.conjunction();
            List<EligibilityStatus> parsed = statuses.stream()
                    .map(String::toUpperCase)
                    .map(EligibilityStatus::valueOf)
                    .collect(Collectors.toList());
            return root.get("status").in(parsed);
        };
    }

    public static Specification<EmployeeEligibility> bySources(List<String> sources) {
        return (root, query, cb) -> {
            if (sources == null || sources.isEmpty()) return cb.conjunction();
            List<EligibilitySource> parsed = sources.stream()
                    .map(String::toUpperCase)
                    .map(EligibilitySource::valueOf)
                    .collect(Collectors.toList());
            return root.get("source").in(parsed);
        };
    }

    public static Specification<EmployeeEligibility> bySearch(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return cb.conjunction();
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";

            var likeNip      = cb.like(cb.lower(root.get("employee").get("nip")), likePattern);
            var likeEmpName  = cb.like(cb.lower(root.get("employee").get("name")), likePattern);
            var likeJobName  = cb.like(cb.lower(root.get("employee").get("jobPosition").get("name")), likePattern);
            var likeCertCode = cb.like(cb.lower(root.get("certificationRule").get("certification").get("code")), likePattern);
            var likeCertName = cb.like(cb.lower(root.get("certificationRule").get("certification").get("name")), likePattern);
            var likeSubName  = cb.like(cb.lower(root.get("certificationRule").get("subField").get("name")), likePattern);

            var likeSource = cb.like(
                    cb.lower(cb.function("str", String.class, root.get("source"))),
                    likePattern
            );

            return cb.or(likeNip, likeEmpName, likeJobName,
                    likeCertCode, likeCertName, likeSubName,
                    likeSource);
        };
    }
}