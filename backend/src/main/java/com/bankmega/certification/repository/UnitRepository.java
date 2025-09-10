package com.bankmega.certification.repository;

import com.bankmega.certification.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    Optional<Unit> findByNameIgnoreCase(String name);
    List<Unit> findAllByOrderByIsActiveDescNameAsc();
    Page<Unit> findByNameContainingIgnoreCase(String name, Pageable pageable);
}