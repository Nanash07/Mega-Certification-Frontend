package com.bankmega.certification.repository;

import com.bankmega.certification.entity.JobCertificationMappingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCertificationMappingHistoryRepository
        extends JpaRepository<JobCertificationMappingHistory, Long>,
        JpaSpecificationExecutor<JobCertificationMappingHistory> {
}
