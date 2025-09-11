package com.bankmega.certification.repository;

import com.bankmega.certification.entity.EmployeeImportLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeImportLogRepository extends JpaRepository<EmployeeImportLog, Long> {
    List<EmployeeImportLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}