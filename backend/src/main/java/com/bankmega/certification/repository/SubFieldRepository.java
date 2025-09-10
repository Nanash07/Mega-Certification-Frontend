package com.bankmega.certification.repository;

import com.bankmega.certification.entity.SubField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubFieldRepository extends JpaRepository<SubField, Long> {

    List<SubField> findByDeletedAtIsNull();

    Optional<SubField> findByIdAndDeletedAtIsNull(Long id);

    List<SubField> findByCertificationIdAndDeletedAtIsNull(Long certificationId);

    boolean existsByCode(String code);

    boolean existsByNameAndCertificationId(String name, Long certificationId);
}