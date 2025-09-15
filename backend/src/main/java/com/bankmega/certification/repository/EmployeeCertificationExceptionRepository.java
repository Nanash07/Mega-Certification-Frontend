package com.bankmega.certification.repository;

import com.bankmega.certification.entity.CertificationRule;
import com.bankmega.certification.entity.Employee;
import com.bankmega.certification.entity.EmployeeCertificationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EmployeeCertificationExceptionRepository
        extends JpaRepository<EmployeeCertificationException, Long>, JpaSpecificationExecutor<EmployeeCertificationException> {

    Optional<EmployeeCertificationException> findByEmployeeAndCertificationRule(Employee employee, CertificationRule rule);

    List<EmployeeCertificationException> findByEmployeeIdAndDeletedAtIsNull(Long employeeId);

    List<EmployeeCertificationException> findByCertificationRuleIdAndDeletedAtIsNull(Long ruleId);

    List<EmployeeCertificationException> findByDeletedAtIsNull();

    Optional<EmployeeCertificationException> findFirstByEmployeeIdAndCertificationRuleId(Long employeeId, Long certificationRuleId);
}
