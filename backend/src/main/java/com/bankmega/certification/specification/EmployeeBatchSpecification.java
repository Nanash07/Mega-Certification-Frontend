package com.bankmega.certification.specification;

import com.bankmega.certification.entity.EmployeeBatch;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeBatchSpecification {

    // 🔹 Soft delete filter
    public static Specification<EmployeeBatch> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    // 🔹 Filter by batchId
    public static Specification<EmployeeBatch> byBatch(Long batchId) {
        return (root, query, cb) ->
                batchId == null ? cb.conjunction() : cb.equal(root.get("batch").get("id"), batchId);
    }

    // 🔹 Filter by status
    public static Specification<EmployeeBatch> byStatus(EmployeeBatch.Status status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    // 🔹 Filter by employee (nama/nip contains)
    public static Specification<EmployeeBatch> bySearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) return cb.conjunction();
            String like = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("employee").get("name")), like),
                    cb.like(cb.lower(root.get("employee").get("nip")), like)
            );
        };
    }
}
