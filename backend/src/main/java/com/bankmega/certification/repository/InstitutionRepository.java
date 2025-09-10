package com.bankmega.certification.repository;

import com.bankmega.certification.entity.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstitutionRepository extends JpaRepository<Institution, Long> {
    List<Institution> findByNameContainingIgnoreCase(String name);
}