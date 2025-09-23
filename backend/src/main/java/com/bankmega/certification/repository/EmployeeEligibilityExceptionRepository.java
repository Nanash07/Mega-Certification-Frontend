package com.bankmega.certification.repository;

import com.bankmega.certification.entity.EmployeeEligibilityException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EmployeeEligibilityExceptionRepository
        extends JpaRepository<EmployeeEligibilityException, Long>, JpaSpecificationExecutor<EmployeeEligibilityException> {

    // 🔹 Cari exception aktif (deletedAt null) by employee + rule
    Optional<EmployeeEligibilityException> findFirstByEmployeeIdAndCertificationRuleIdAndDeletedAtIsNull(
            Long employeeId, Long certificationRuleId);

    // 🔹 Cari semua exception aktif milik employee
    List<EmployeeEligibilityException> findByEmployeeIdAndDeletedAtIsNull(Long employeeId);

    // 🔹 Cari semua exception aktif milik rule tertentu
    List<EmployeeEligibilityException> findByCertificationRuleIdAndDeletedAtIsNull(Long ruleId);

    // 🔹 Cari semua exception aktif
    List<EmployeeEligibilityException> findByDeletedAtIsNull();

    // 🔹 Cari kombinasi employee + rule (termasuk yang soft delete)
    Optional<EmployeeEligibilityException> findFirstByEmployeeIdAndCertificationRuleId(
            Long employeeId, Long certificationRuleId);
}