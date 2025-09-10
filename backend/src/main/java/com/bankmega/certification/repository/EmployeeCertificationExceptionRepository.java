package com.bankmega.certification.repository;

import com.bankmega.certification.entity.EmployeeCertificationException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeCertificationExceptionRepository extends JpaRepository<EmployeeCertificationException, Long> {
    List<EmployeeCertificationException> findAllByDeletedAtIsNull();
    List<EmployeeCertificationException> findByEmployeeIdAndDeletedAtIsNull(Long employeeId);
}