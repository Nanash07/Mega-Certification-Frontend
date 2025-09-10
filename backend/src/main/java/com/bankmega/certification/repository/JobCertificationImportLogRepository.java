package com.bankmega.certification.repository;

import com.bankmega.certification.entity.JobCertificationImportLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobCertificationImportLogRepository extends JpaRepository<JobCertificationImportLog, Long> {
    List<JobCertificationImportLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}