package com.bankmega.certification.repository;

import com.bankmega.certification.entity.Division;
import com.bankmega.certification.entity.Employee;
import com.bankmega.certification.entity.JobPosition;
import com.bankmega.certification.entity.Regional;
import com.bankmega.certification.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    List<Employee> findByDeletedAtIsNull();
    Optional<Employee> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByNipAndDeletedAtIsNull(String nip);
    Optional<Employee> findByNipAndDeletedAtIsNull(String nip);

    // reactivate kalau soft delete
    Optional<Employee> findByNip(String nip);

    boolean existsByRegional(Regional regional);
    boolean existsByDivision(Division division);
    boolean existsByUnit(Unit unit);
    boolean existsByJobPosition(JobPosition jobPosition);
}