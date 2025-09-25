package com.bankmega.certification.service;

import com.bankmega.certification.dto.EmployeeCertificationRequest;
import com.bankmega.certification.dto.EmployeeCertificationResponse;
import com.bankmega.certification.entity.*;
import com.bankmega.certification.repository.*;
import com.bankmega.certification.specification.EmployeeCertificationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeCertificationService {

    private final EmployeeCertificationRepository repo;
    private final EmployeeRepository employeeRepo;
    private final CertificationRuleRepository ruleRepo;
    private final InstitutionRepository institutionRepo;

    // ================== Mapper ==================
    private EmployeeCertificationResponse toResponse(EmployeeCertification ec) {
        return EmployeeCertificationResponse.builder()
                .id(ec.getId())
                .employeeId(ec.getEmployee().getId())
                .nip(ec.getEmployee().getNip())
                .employeeName(ec.getEmployee().getName())
                .jobPositionTitle(
                        ec.getEmployee().getJobPosition() != null
                                ? ec.getEmployee().getJobPosition().getName()
                                : null
                )
                .certificationRuleId(ec.getCertificationRule().getId())
                .certificationName(ec.getCertificationRule().getCertification().getName())
                .certificationCode(ec.getCertificationRule().getCertification().getCode())
                .certificationLevelName(
                        ec.getCertificationRule().getCertificationLevel() != null
                                ? ec.getCertificationRule().getCertificationLevel().getName()
                                : null
                )
                .certificationLevelLevel(
                        ec.getCertificationRule().getCertificationLevel() != null
                                ? ec.getCertificationRule().getCertificationLevel().getLevel()
                                : null
                )
                .subFieldCode(
                        ec.getCertificationRule().getSubField() != null
                                ? ec.getCertificationRule().getSubField().getCode()
                                : null
                )
                .subFieldName(
                        ec.getCertificationRule().getSubField() != null
                                ? ec.getCertificationRule().getSubField().getName()
                                : null
                )
                .institutionId(ec.getInstitution() != null ? ec.getInstitution().getId() : null)
                .institutionName(ec.getInstitution() != null ? ec.getInstitution().getName() : null)
                .certNumber(ec.getCertNumber())
                .certDate(ec.getCertDate())
                .validFrom(ec.getValidFrom())
                .validUntil(ec.getValidUntil())
                .reminderDate(ec.getReminderDate())
                .fileUrl(ec.getFileUrl())
                .status(ec.getStatus())
                .processType(ec.getProcessType())
                .createdAt(ec.getCreatedAt())
                .updatedAt(ec.getUpdatedAt())
                .deletedAt(ec.getDeletedAt())
                .build();
    }

    // ================== Helpers ==================
    private void updateValidity(EmployeeCertification ec) {
        LocalDate certDate = ec.getCertDate();
        if (certDate != null) {
            ec.setValidFrom(certDate);
            CertificationRule rule = ec.getCertificationRule();

            if (rule != null && rule.getValidityMonths() != null) {
                ec.setValidUntil(certDate.plusMonths(rule.getValidityMonths()));
            }

            if (rule != null && rule.getReminderMonths() != null && ec.getValidUntil() != null) {
                ec.setReminderDate(ec.getValidUntil().minusMonths(rule.getReminderMonths()));
            }
        }
    }

    private void updateStatus(EmployeeCertification ec) {
        LocalDate today = LocalDate.now();

        if (ec.getValidUntil() == null) {
            ec.setStatus(EmployeeCertification.Status.NOT_YET_CERTIFIED);
        } else if (today.isAfter(ec.getValidUntil())) {
            ec.setStatus(EmployeeCertification.Status.EXPIRED);
        } else if (ec.getReminderDate() != null && !today.isBefore(ec.getReminderDate())) {
            ec.setStatus(EmployeeCertification.Status.DUE);
        } else {
            ec.setStatus(EmployeeCertification.Status.ACTIVE);
        }
    }

    // ================== Create ==================
    @Transactional
    public EmployeeCertificationResponse create(EmployeeCertificationRequest req) {
        Employee employee = employeeRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        CertificationRule rule = ruleRepo.findById(req.getCertificationRuleId())
                .orElseThrow(() -> new RuntimeException("Certification Rule not found"));

        Institution institution = req.getInstitutionId() != null
                ? institutionRepo.findById(req.getInstitutionId()).orElse(null)
                : null;

        // 🔹 Cek duplikat
        repo.findFirstByEmployeeIdAndCertificationRuleIdAndDeletedAtIsNull(employee.getId(), rule.getId())
                .ifPresent(ec -> {
                    throw new RuntimeException("Certification already exists for this employee & rule");
                });

        EmployeeCertification ec = EmployeeCertification.builder()
                .employee(employee)
                .certificationRule(rule)
                .institution(institution)
                .certNumber(req.getCertNumber())
                .certDate(req.getCertDate())
                .fileUrl(req.getFileUrl())
                .processType(req.getProcessType())
                .status(EmployeeCertification.Status.NOT_YET_CERTIFIED)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        updateValidity(ec);
        updateStatus(ec);

        return toResponse(repo.save(ec));
    }

    // ================== Update ==================
    @Transactional
    public EmployeeCertificationResponse update(Long id, EmployeeCertificationRequest req) {
        EmployeeCertification ec = repo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Certification not found"));

        if (req.getCertificationRuleId() != null) {
            CertificationRule rule = ruleRepo.findById(req.getCertificationRuleId())
                    .orElseThrow(() -> new RuntimeException("Certification Rule not found"));
            ec.setCertificationRule(rule);
        }

        if (req.getInstitutionId() != null) {
            Institution institution = institutionRepo.findById(req.getInstitutionId()).orElse(null);
            ec.setInstitution(institution);
        }

        ec.setCertNumber(req.getCertNumber());
        ec.setCertDate(req.getCertDate());
        ec.setFileUrl(req.getFileUrl());
        ec.setProcessType(req.getProcessType());
        ec.setUpdatedAt(Instant.now());

        updateValidity(ec);
        updateStatus(ec);

        return toResponse(repo.save(ec));
    }

    // ================== Soft Delete ==================
    @Transactional
    public void softDelete(Long id) {
        EmployeeCertification ec = repo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Certification not found"));
        ec.setDeletedAt(Instant.now());
        ec.setStatus(EmployeeCertification.Status.INVALID);
        ec.setUpdatedAt(Instant.now());
        repo.save(ec);
    }

    // ================== Detail ==================
    @Transactional(readOnly = true)
    public EmployeeCertificationResponse getDetail(Long id) {
        EmployeeCertification ec = repo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Certification not found"));
        return toResponse(ec);
    }

    // ================== Paging + Filter ==================
    @Transactional(readOnly = true)
    public Page<EmployeeCertificationResponse> getPagedFiltered(
            List<Long> employeeIds,
            List<String> certCodes,
            List<Integer> levels,
            List<String> subCodes,
            List<Long> institutionIds,
            List<String> statuses,
            String search,
            LocalDate certDateStart,
            LocalDate certDateEnd,
            LocalDate validUntilStart,
            LocalDate validUntilEnd,
            Pageable pageable
    ) {
        Specification<EmployeeCertification> spec = EmployeeCertificationSpecification.notDeleted()
            .and(EmployeeCertificationSpecification.byEmployeeIds(employeeIds))
            .and(EmployeeCertificationSpecification.byCertCodes(certCodes))
            .and(EmployeeCertificationSpecification.byLevels(levels))
            .and(EmployeeCertificationSpecification.bySubCodes(subCodes))
            .and(EmployeeCertificationSpecification.byInstitutionIds(institutionIds))
            .and(EmployeeCertificationSpecification.byStatuses(statuses))
            .and(EmployeeCertificationSpecification.bySearch(search))
            .and(EmployeeCertificationSpecification.byCertDateRange(certDateStart, certDateEnd))   // 🔥 NEW
            .and(EmployeeCertificationSpecification.byValidUntilRange(validUntilStart, validUntilEnd)); // 🔥 NEW

        return repo.findAll(spec, pageable).map(this::toResponse);
    }
}