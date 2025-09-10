package com.bankmega.certification.repository;

import com.bankmega.certification.entity.EmployeeEligibility;
import com.bankmega.certification.entity.Employee;
import com.bankmega.certification.entity.CertificationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EmployeeEligibilityRepository extends JpaRepository<EmployeeEligibility, Long>, JpaSpecificationExecutor<EmployeeEligibility> {

    Optional<EmployeeEligibility> findByEmployeeAndCertificationRule(Employee employee, CertificationRule rule);

    List<EmployeeEligibility> findByEmployeeAndDeletedAtIsNull(Employee employee);

    List<EmployeeEligibility> findByCertificationRuleAndDeletedAtIsNull(CertificationRule rule);
}
