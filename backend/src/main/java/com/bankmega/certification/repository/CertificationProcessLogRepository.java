package com.bankmega.certification.repository;

import com.bankmega.certification.entity.CertificationProcessLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificationProcessLogRepository extends JpaRepository<CertificationProcessLog, Long> {
    List<CertificationProcessLog> findByEmployeeCertificationIdOrderByCreatedAtDesc(Long certificationId);
}
