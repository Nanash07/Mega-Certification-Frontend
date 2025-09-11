package com.bankmega.certification.service;

import com.bankmega.certification.dto.EmployeeRequest;
import com.bankmega.certification.dto.EmployeeResponse;
import com.bankmega.certification.entity.*;
import com.bankmega.certification.exception.ConflictException;
import com.bankmega.certification.exception.NotFoundException;
import com.bankmega.certification.repository.*;
import com.bankmega.certification.specification.EmployeeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repo;
    private final RegionalRepository regionalRepo;
    private final DivisionRepository divisionRepo;
    private final UnitRepository unitRepo;
    private final JobPositionRepository jobPositionRepo;

    // 🔹 Paging + Filter + Search
    public Page<EmployeeResponse> search(
            String search,
            List<Long> regionalIds,
            List<Long> divisionIds,
            List<Long> unitIds,
            List<Long> jobPositionIds,
            Pageable pageable
    ) {
        Specification<Employee> spec = EmployeeSpecification.notDeleted()
                .and(EmployeeSpecification.bySearch(search))
                .and(EmployeeSpecification.byRegionalIds(regionalIds))
                .and(EmployeeSpecification.byDivisionIds(divisionIds))
                .and(EmployeeSpecification.byUnitIds(unitIds))
                .and(EmployeeSpecification.byJobPositionIds(jobPositionIds));

        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(
                            Sort.Order.asc("regional.name"),
                            Sort.Order.asc("division.name"),
                            Sort.Order.asc("unit.name"),
                            Sort.Order.asc("jobPosition.name"),
                            Sort.Order.asc("name")
                    )
            );
        }

        return repo.findAll(spec, pageable).map(this::toResponse);
    }

    public EmployeeResponse getById(Long id) {
        Employee emp = repo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id " + id));
        return toResponse(emp);
    }

    public EmployeeResponse create(EmployeeRequest req) {
        if (repo.existsByNipAndDeletedAtIsNull(req.getNip())) {
            throw new ConflictException("NIP " + req.getNip() + " is already used");
        }
        Employee emp = mapRequestToEntity(new Employee(), req);
        return toResponse(repo.save(emp));
    }

    public EmployeeResponse update(Long id, EmployeeRequest req) {
        Employee emp = repo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id " + id));

        if (!emp.getNip().equals(req.getNip()) && repo.existsByNipAndDeletedAtIsNull(req.getNip())) {
            throw new ConflictException("NIP " + req.getNip() + " is already used");
        }

        emp = mapRequestToEntity(emp, req);
        return toResponse(repo.save(emp));
    }

    public void softDelete(Long id) {
        Employee emp = repo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id " + id));
        emp.setDeletedAt(Instant.now());
        repo.save(emp);
    }

    private Employee mapRequestToEntity(Employee emp, EmployeeRequest req) {
        Regional reg = regionalRepo.findById(req.getRegionalId())
                .orElseThrow(() -> new NotFoundException("Regional not found: " + req.getRegionalId()));
        Division div = divisionRepo.findById(req.getDivisionId())
                .orElseThrow(() -> new NotFoundException("Division not found: " + req.getDivisionId()));
        Unit unit = unitRepo.findById(req.getUnitId())
                .orElseThrow(() -> new NotFoundException("Unit not found: " + req.getUnitId()));
        JobPosition job = jobPositionRepo.findById(req.getJobPositionId())
                .orElseThrow(() -> new NotFoundException("JobPosition not found: " + req.getJobPositionId()));

        emp.setNip(req.getNip());
        emp.setName(req.getName());
        emp.setEmail(req.getEmail());
        emp.setGender(req.getGender());
        emp.setRegional(reg);
        emp.setDivision(div);
        emp.setUnit(unit);
        emp.setJobPosition(job);
        emp.setJoinDate(req.getJoinDate());
        emp.setStatus(req.getStatus());
        emp.setPhotoUrl(req.getPhotoUrl());

        return emp;
    }

    private EmployeeResponse toResponse(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .nip(e.getNip())
                .name(e.getName())
                .email(e.getEmail())
                .gender(e.getGender())
                .regionalId(e.getRegional() != null ? e.getRegional().getId() : null)
                .regionalName(e.getRegional() != null ? e.getRegional().getName() : null)
                .divisionId(e.getDivision() != null ? e.getDivision().getId() : null)
                .divisionName(e.getDivision() != null ? e.getDivision().getName() : null)
                .unitId(e.getUnit() != null ? e.getUnit().getId() : null)
                .unitName(e.getUnit() != null ? e.getUnit().getName() : null)
                .jobPositionId(e.getJobPosition() != null ? e.getJobPosition().getId() : null)
                .jobName(e.getJobPosition() != null ? e.getJobPosition().getName() : null)
                .joinDate(e.getJoinDate())
                .status(e.getStatus())
                .photoUrl(e.getPhotoUrl())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .deletedAt(e.getDeletedAt())
                .build();
    }
}