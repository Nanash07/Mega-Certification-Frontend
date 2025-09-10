package com.bankmega.certification.repository;

import com.bankmega.certification.entity.EmployeeHistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, Long> {
    List<EmployeeHistory> findByEmployee_IdOrderByActionAtDesc(Long employeeId);
}