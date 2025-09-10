package com.bankmega.certification.repository;

import com.bankmega.certification.entity.Regional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface RegionalRepository extends JpaRepository<Regional, Long> {
    Optional<Regional> findByNameIgnoreCase(String name);
    List<Regional> findAllByOrderByIsActiveDescNameAsc();
    Page<Regional> findByNameContainingIgnoreCase(String name, Pageable pageable);
}