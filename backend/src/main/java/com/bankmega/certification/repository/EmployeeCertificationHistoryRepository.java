package com.bankmega.certification.repository;

import com.bankmega.certification.entity.EmployeeCertificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeCertificationHistoryRepository extends JpaRepository<EmployeeCertificationHistory, Long> {
    List<EmployeeCertificationHistory> findByEmployeeCertificationIdOrderByActionAtDesc(Long certificationId);
}
