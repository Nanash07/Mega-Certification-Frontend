package com.bankmega.certification.repository;

import com.bankmega.certification.entity.EmployeeCertificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeCertificationHistoryRepository extends JpaRepository<EmployeeCertificationHistory, Long> {

    // 🔹 Ambil semua history berdasarkan employeeCertificationId urut paling baru dulu
    List<EmployeeCertificationHistory> findByEmployeeCertificationIdOrderByActionAtDesc(Long certificationId);
}