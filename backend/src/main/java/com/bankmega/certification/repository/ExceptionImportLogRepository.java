package com.bankmega.certification.repository;

import com.bankmega.certification.entity.ExceptionImportLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExceptionImportLogRepository extends JpaRepository<ExceptionImportLog, Long> {
    List<ExceptionImportLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}