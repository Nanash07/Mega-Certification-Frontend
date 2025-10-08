package com.bankmega.certification.repository;

import com.bankmega.certification.entity.EmployeeEligibility;
import com.bankmega.certification.entity.Employee;
import com.bankmega.certification.entity.CertificationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmployeeEligibilityRepository
        extends JpaRepository<EmployeeEligibility, Long>, JpaSpecificationExecutor<EmployeeEligibility> {

    // ==== Find by Employee + Rule + Source (baru, lebih spesifik) ====
    Optional<EmployeeEligibility> findByEmployeeAndCertificationRuleAndSource(
            Employee employee,
            CertificationRule rule,
            EmployeeEligibility.EligibilitySource source);

    List<EmployeeEligibility> findByCertificationRule_IdAndIsActiveTrueAndDeletedAtIsNull(Long certRuleId);

    // ==== Employee based ====
    List<EmployeeEligibility> findByEmployeeAndDeletedAtIsNull(Employee employee);

    List<EmployeeEligibility> findByEmployeeIdAndDeletedAtIsNull(Long employeeId);

    List<EmployeeEligibility> findByEmployeeIdInAndDeletedAtIsNull(Set<Long> employeeIds);

    // ==== Rule based ====
    List<EmployeeEligibility> findByCertificationRuleAndDeletedAtIsNull(CertificationRule rule);

    List<EmployeeEligibility> findByCertificationRuleIdAndDeletedAtIsNull(Long ruleId);

    // ==== Active status ====
    List<EmployeeEligibility> findByIsActiveTrueAndDeletedAtIsNull();

    List<EmployeeEligibility> findByIsActiveFalseAndDeletedAtIsNull();

    // ==== Default (not deleted) ====
    List<EmployeeEligibility> findByDeletedAtIsNull();

    List<EmployeeEligibility> findByEmployee_IdAndDeletedAtIsNull(Long employeeId);
}