package com.bankmega.certification.repository;

import com.bankmega.certification.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    // 🔹 Cari role by nama (case-sensitive)
    Optional<Role> findByName(String name);

    // 🔹 Cari role by nama (case-insensitive)
    Optional<Role> findByNameIgnoreCase(String name);

    // 🔹 Cek kalau nama role udah ada (case-insensitive)
    boolean existsByNameIgnoreCase(String name);
}