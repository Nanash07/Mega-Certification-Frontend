package com.bankmega.certification.repository;

import com.bankmega.certification.entity.RefreshmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshmentTypeRepository extends JpaRepository<RefreshmentType, Long> {
    Optional<RefreshmentType> findByName(String name);
    boolean existsByName(String name);
}