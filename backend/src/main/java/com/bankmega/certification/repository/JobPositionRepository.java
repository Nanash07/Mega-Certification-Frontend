package com.bankmega.certification.repository;

import com.bankmega.certification.entity.JobPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {
    Optional<JobPosition> findByNameIgnoreCase(String name);
    List<JobPosition> findAllByOrderByIsActiveDescNameAsc();
    Page<JobPosition> findByNameContainingIgnoreCase(String name, Pageable pageable);
}