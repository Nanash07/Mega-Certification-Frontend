package com.bankmega.certification.repository;

import com.bankmega.certification.entity.CertificationRule;
import com.bankmega.certification.entity.Employee;
import com.bankmega.certification.entity.EmployeeEligibilityException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EmployeeEligibilityExceptionRepository
        extends JpaRepository<EmployeeEligibilityException, Long>, JpaSpecificationExecutor<EmployeeEligibilityException> {

    // ✅ Cari exception aktif (deletedAt null) by employee + rule
    Optional<EmployeeEligibilityException> findFirstByEmployeeAndCertificationRuleAndDeletedAtIsNull(
            Employee employee, CertificationRule rule);

    // ✅ Cari exception aktif by employeeId + ruleId
    Optional<EmployeeEligibilityException> findFirstByEmployeeIdAndCertificationRuleIdAndDeletedAtIsNull(
            Long employeeId, Long certificationRuleId);

    // ✅ Semua exception aktif milik employee tertentu
    List<EmployeeEligibilityException> findByEmployeeIdAndDeletedAtIsNull(Long employeeId);

    // ✅ Semua exception aktif milik rule tertentu
    List<EmployeeEligibilityException> findByCertificationRuleIdAndDeletedAtIsNull(Long ruleId);

    // ✅ Semua exception aktif
    List<EmployeeEligibilityException> findByDeletedAtIsNull();

    // termasuk yang sudah soft delete
    Optional<EmployeeEligibilityException> findFirstByEmployeeIdAndCertificationRuleId(Long employeeId, Long certificationRuleId);
}