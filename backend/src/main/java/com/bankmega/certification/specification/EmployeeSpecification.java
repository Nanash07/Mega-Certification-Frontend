package com.bankmega.certification.specification;

import com.bankmega.certification.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecification {
    public static Specification<Employee> search(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return cb.conjunction();
            String like = "%" + q.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), like),
                cb.like(cb.lower(root.get("nip")), like),
                cb.like(cb.lower(root.get("email")), like)
            );
        };
    }

    public static Specification<Employee> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }
}