package com.bankmega.certification.repository;

import com.bankmega.certification.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    Optional<Role> findByName(String name);
    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.id = :roleId AND u.deletedAt IS NULL")
    long countUsersByRoleId(@Param("roleId") Long roleId);
}