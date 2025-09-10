package com.bankmega.certification.repository;

import com.bankmega.certification.entity.Division;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface DivisionRepository extends JpaRepository<Division, Long> {
    Optional<Division> findByNameIgnoreCase(String name);
    List<Division> findAllByOrderByIsActiveDescNameAsc();
    Page<Division> findByNameContainingIgnoreCase(String name, Pageable pageable);
}