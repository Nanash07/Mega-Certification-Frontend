package com.bankmega.certification.service;

import com.bankmega.certification.dto.EmployeeExceptionResponse;
import com.bankmega.certification.entity.CertificationRule;
import com.bankmega.certification.entity.Employee;
import com.bankmega.certification.entity.EmployeeCertificationException;
import com.bankmega.certification.repository.CertificationRuleRepository;
import com.bankmega.certification.repository.EmployeeCertificationExceptionRepository;
import com.bankmega.certification.repository.EmployeeRepository;
import com.bankmega.certification.specification.EmployeeExceptionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeExceptionService {

    private final EmployeeCertificationExceptionRepository exceptionRepo;
    private final EmployeeRepository employeeRepo;
    private final CertificationRuleRepository ruleRepo;

    // 🔹 Mapper Entity → DTO
    private EmployeeExceptionResponse toResponse(EmployeeCertificationException e) {
        Employee emp = e.getEmployee();
        CertificationRule rule = e.getCertificationRule();

        return EmployeeExceptionResponse.builder()
                .id(e.getId())
                .employeeId(emp != null ? emp.getId() : null)
                .employeeName(emp != null ? emp.getName() : null)
                .nip(emp != null ? emp.getNip() : null)
                .jobPositionTitle(emp != null && emp.getJobPosition() != null
                        ? emp.getJobPosition().getName() : null)

                .certificationRuleId(rule != null ? rule.getId() : null)
                .certificationCode(rule != null ? rule.getCertification().getCode() : null)
                .certificationName(rule != null ? rule.getCertification().getName() : null)
                .certificationLevelName(rule != null && rule.getCertificationLevel() != null
                        ? rule.getCertificationLevel().getName() : null)
                .certificationLevelLevel(rule != null && rule.getCertificationLevel() != null
                        ? rule.getCertificationLevel().getLevel() : null)
                .subFieldName(rule != null && rule.getSubField() != null
                        ? rule.getSubField().getName() : null)
                .subFieldCode(rule != null && rule.getSubField() != null
                        ? rule.getSubField().getCode() : null)

                .isActive(e.getIsActive())
                .notes(e.getNotes())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    // 🔹 Paging + Filter + Search
    @Transactional(readOnly = true)
    public Page<EmployeeExceptionResponse> getPagedFiltered(
            List<Long> jobIds,
            List<String> certCodes,
            List<Integer> levels,
            List<String> subCodes,
            String status,
            String search,
            Pageable pageable
    ) {
        Specification<EmployeeCertificationException> spec = EmployeeExceptionSpecification.notDeleted()
                .and(EmployeeExceptionSpecification.byJobIds(jobIds))
                .and(EmployeeExceptionSpecification.byCertCodes(certCodes))
                .and(EmployeeExceptionSpecification.byLevels(levels))
                .and(EmployeeExceptionSpecification.bySubCodes(subCodes))
                .and(EmployeeExceptionSpecification.byStatus(status)) // ✅ filter status
                .and(EmployeeExceptionSpecification.bySearch(search));

        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(
                            Sort.Order.asc("employee.jobPosition.name"),
                            Sort.Order.asc("certificationRule.certification.code"),
                            Sort.Order.asc("certificationRule.certificationLevel.level"),
                            Sort.Order.asc("certificationRule.subField.code")
                    )
            );
        }

        return exceptionRepo.findAll(spec, pageable).map(this::toResponse);
    }

    // 🔹 Get exceptions by employee
    @Transactional(readOnly = true)
    public List<EmployeeExceptionResponse> getByEmployee(Long employeeId) {
        return exceptionRepo.findByEmployeeIdAndDeletedAtIsNull(employeeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 🔹 Create new exception
    @Transactional
public EmployeeExceptionResponse create(Long employeeId, Long certificationRuleId, String notes) {
    Employee employee = employeeRepo.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
    CertificationRule rule = ruleRepo.findById(certificationRuleId)
            .orElseThrow(() -> new RuntimeException("Certification rule not found"));

    // cek apakah pernah ada exception untuk kombinasi employee + rule
    EmployeeCertificationException existing = exceptionRepo
            .findFirstByEmployeeIdAndCertificationRuleId(employeeId, certificationRuleId)
            .orElse(null);

    if (existing != null) {
        if (existing.getDeletedAt() != null) {
            // 🔹 re-activate kalau pernah soft delete
            existing.setDeletedAt(null);
            existing.setIsActive(true);
            existing.setNotes(notes);
            existing.setUpdatedAt(Instant.now());
            return toResponse(exceptionRepo.save(existing));
        } else {
            throw new RuntimeException("Exception already exists and active");
        }
    }

    // kalau belum pernah ada → create baru
    EmployeeCertificationException exception = EmployeeCertificationException.builder()
            .employee(employee)
            .certificationRule(rule)
            .isActive(true)
            .notes(notes)
            .build();

    return toResponse(exceptionRepo.save(exception));
}

    // 🔹 Update notes
    @Transactional
    public EmployeeExceptionResponse updateNotes(Long id, String notes) {
        EmployeeCertificationException exception = exceptionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Exception not found"));
        exception.setNotes(notes);
        return toResponse(exceptionRepo.save(exception));
    }

    // 🔹 Toggle active/inactive
    @Transactional
    public EmployeeExceptionResponse toggleActive(Long id) {
        EmployeeCertificationException exception = exceptionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Exception not found"));
        exception.setIsActive(!Boolean.TRUE.equals(exception.getIsActive()));
        exception.setUpdatedAt(Instant.now());
        return toResponse(exceptionRepo.save(exception));
    }

    // 🔹 Soft delete
    @Transactional
    public void softDelete(Long id) {
        EmployeeCertificationException exception = exceptionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Exception not found"));
        exception.setIsActive(false);
        exception.setDeletedAt(Instant.now());
        exceptionRepo.save(exception);
    }
}