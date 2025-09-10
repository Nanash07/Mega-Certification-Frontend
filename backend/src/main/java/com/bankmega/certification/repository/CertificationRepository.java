package com.bankmega.certification.repository;

import com.bankmega.certification.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    Optional<Certification> findByCode(String code);
    Optional<Certification> findByName(String name);
    List<Certification> findByDeletedAtIsNull();
    Optional<Certification> findByIdAndDeletedAtIsNull(Long id);
    boolean existsByCode(String code);
    boolean existsByName(String name);
}