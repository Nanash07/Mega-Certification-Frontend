package com.bankmega.certification.repository;

import com.bankmega.certification.entity.EmployeeEligibilityExceptionImportLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EligibilityExceptionImportLogRepository extends JpaRepository<EmployeeEligibilityExceptionImportLog, Long> {
    List<EmployeeEligibilityExceptionImportLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}