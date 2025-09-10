package com.bankmega.certification.repository;

import com.bankmega.certification.entity.CertificationLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificationLevelRepository extends JpaRepository<CertificationLevel, Long> {

    List<CertificationLevel> findByDeletedAtIsNull();

    Optional<CertificationLevel> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByLevel(Integer level);

    boolean existsByName(String name);
}