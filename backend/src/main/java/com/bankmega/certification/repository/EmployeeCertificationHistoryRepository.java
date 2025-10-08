package com.bankmega.certification.repository;

import com.bankmega.certification.entity.EmployeeCertificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmployeeCertificationHistoryRepository
        extends JpaRepository<EmployeeCertificationHistory, Long>,
        JpaSpecificationExecutor<EmployeeCertificationHistory> {

    // Ambil semua history by certification id (kalau butuh non-paging)
    java.util.List<EmployeeCertificationHistory> findByEmployeeCertificationIdOrderByActionAtDesc(Long certificationId);

    // Ambil history terakhir (buat snapshot comparison)
    Optional<EmployeeCertificationHistory> findTopByEmployeeCertificationIdOrderByActionAtDesc(Long certificationId);
}
