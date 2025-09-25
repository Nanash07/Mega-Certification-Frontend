// src/main/java/com/bankmega/certification/repository/BatchRepository.java

package com.bankmega.certification.repository;

import com.bankmega.certification.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long>, JpaSpecificationExecutor<Batch> {
    Optional<Batch> findByIdAndDeletedAtIsNull(Long id);
}