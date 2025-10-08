package com.bankmega.certification.repository;

import com.bankmega.certification.entity.EmployeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeHistoryRepository
        extends JpaRepository<EmployeeHistory, Long>, JpaSpecificationExecutor<EmployeeHistory> {

    List<EmployeeHistory> findByEmployee_IdOrderByActionAtDesc(Long employeeId);

    Optional<EmployeeHistory> findTopByEmployee_IdOrderByActionAtDesc(Long employeeId);
}
