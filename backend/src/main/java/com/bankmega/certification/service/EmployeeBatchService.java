package com.bankmega.certification.service;

import com.bankmega.certification.dto.EmployeeBatchResponse;
import com.bankmega.certification.dto.EmployeeEligibilityResponse;
import com.bankmega.certification.entity.*;
import com.bankmega.certification.exception.NotFoundException;
import com.bankmega.certification.repository.*;
import com.bankmega.certification.specification.EmployeeBatchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeBatchService {

    private final EmployeeBatchRepository repo;
    private final BatchRepository batchRepo;
    private final EmployeeRepository employeeRepo;
    private final EmployeeEligibilityRepository eligibilityRepo;
    private final EmployeeCertificationRepository certificationRepo;

    // ================== MAPPER ==================
    private EmployeeBatchResponse toResponse(EmployeeBatch eb) {
        return EmployeeBatchResponse.builder()
                .id(eb.getId())
                .employeeId(eb.getEmployee().getId())
                .employeeNip(eb.getEmployee().getNip())
                .employeeName(eb.getEmployee().getName())
                .batchId(eb.getBatch().getId())
                .batchName(eb.getBatch().getBatchName())
                .status(eb.getStatus())
                .registrationDate(eb.getRegistrationDate())
                .attendedAt(eb.getAttendedAt())
                .resultDate(eb.getResultDate())
                .notes(eb.getNotes())
                .createdAt(eb.getCreatedAt())
                .updatedAt(eb.getUpdatedAt())
                .build();
    }

    private EmployeeEligibilityResponse toEligibilityResponse(EmployeeEligibility e) {
        Employee emp = e.getEmployee();
        return EmployeeEligibilityResponse.builder()
                .employeeId(emp != null ? emp.getId() : null)
                .nip(emp != null ? emp.getNip() : null)
                .employeeName(emp != null ? emp.getName() : null)
                .jobPositionTitle(emp != null && emp.getJobPosition() != null ? emp.getJobPosition().getName() : null)
                .certificationRuleId(e.getCertificationRule() != null ? e.getCertificationRule().getId() : null)
                .certificationCode(
                        e.getCertificationRule() != null && e.getCertificationRule().getCertification() != null
                                ? e.getCertificationRule().getCertification().getCode()
                                : null
                )
                .certificationName(
                        e.getCertificationRule() != null && e.getCertificationRule().getCertification() != null
                                ? e.getCertificationRule().getCertification().getName()
                                : null
                )
                .status(e.getStatus() != null ? e.getStatus().name() : null)
                .isActive(e.getIsActive())
                .build();
    }

    // ================== LIST ==================
    @Transactional(readOnly = true)
    public List<EmployeeBatchResponse> getByBatch(Long batchId) {
        return repo.findByBatch_IdAndDeletedAtIsNull(batchId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ================== SEARCH + PAGING ==================
    @Transactional(readOnly = true)
    public Page<EmployeeBatchResponse> search(
            Long batchId,
            String search,
            EmployeeBatch.Status status,
            Pageable pageable
    ) {
        Specification<EmployeeBatch> spec = EmployeeBatchSpecification.notDeleted()
                .and(EmployeeBatchSpecification.byBatch(batchId))
                .and(EmployeeBatchSpecification.byStatus(status))
                .and(EmployeeBatchSpecification.bySearch(search));

        // ✅ Default sort by employee.nip kalau FE gak kirim sort
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Order.asc("employee.nip"))
            );
        }

        return repo.findAll(spec, pageable).map(this::toResponse);
    }

    // ================== ADD SINGLE PARTICIPANT ==================
    @Transactional
    public EmployeeBatchResponse addParticipant(Long batchId, Long employeeId) {
        Batch batch = batchRepo.findByIdAndDeletedAtIsNull(batchId)
                .orElseThrow(() -> new NotFoundException("Batch not found"));
        Employee emp = employeeRepo.findByIdAndDeletedAtIsNull(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        // Quota check
        long currentCount = repo.countByBatch_IdAndDeletedAtIsNull(batchId);
        if (batch.getQuota() != null && currentCount >= batch.getQuota()) {
            throw new IllegalStateException("Quota batch sudah penuh");
        }

        return repo.findByBatch_IdAndEmployee_Id(batchId, employeeId)
                .map(eb -> {
                    if (eb.getDeletedAt() == null) {
                        throw new IllegalStateException("Peserta sudah ada di batch ini");
                    }
                    // restore record lama
                    eb.setDeletedAt(null);
                    eb.setStatus(EmployeeBatch.Status.REGISTERED);
                    eb.setRegistrationDate(LocalDate.now());
                    eb.setUpdatedAt(Instant.now());
                    return toResponse(repo.save(eb));
                })
                .orElseGet(() -> {
                    EmployeeBatch eb = EmployeeBatch.builder()
                            .batch(batch)
                            .employee(emp)
                            .status(EmployeeBatch.Status.REGISTERED)
                            .registrationDate(LocalDate.now())
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build();
                    return toResponse(repo.save(eb));
                });
    }

    // ================== ADD MULTIPLE PARTICIPANTS ==================
    @Transactional
    public List<EmployeeBatchResponse> addParticipantsBulk(Long batchId, List<Long> employeeIds) {
        Batch batch = batchRepo.findByIdAndDeletedAtIsNull(batchId)
                .orElseThrow(() -> new NotFoundException("Batch not found"));

        long currentCount = repo.countByBatch_IdAndDeletedAtIsNull(batchId);
        if (batch.getQuota() != null && currentCount + employeeIds.size() > batch.getQuota()) {
            throw new IllegalStateException("Jumlah peserta melebihi quota batch");
        }

        List<EmployeeBatchResponse> responses = new ArrayList<>();
        for (Long empId : employeeIds) {
            Employee emp = employeeRepo.findByIdAndDeletedAtIsNull(empId)
                    .orElseThrow(() -> new NotFoundException("Employee not found"));

            boolean exists = repo.existsByBatch_IdAndEmployee_IdAndDeletedAtIsNull(batchId, empId);
            if (exists) {
                continue; // skip kalau sudah ada
            }

            EmployeeBatch eb = EmployeeBatch.builder()
                    .batch(batch)
                    .employee(emp)
                    .status(EmployeeBatch.Status.REGISTERED)
                    .registrationDate(LocalDate.now())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            responses.add(toResponse(repo.save(eb)));
        }

        return responses;
    }

    // ================== UPDATE STATUS ==================
    @Transactional
    public EmployeeBatchResponse updateStatus(
            Long id,
            EmployeeBatch.Status status,
            Integer score,
            String notes
    ) {
        EmployeeBatch eb = repo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("EmployeeBatch not found"));

        // Validasi transisi
        EmployeeBatch.Status current = eb.getStatus();
        if (status == EmployeeBatch.Status.ATTENDED && current != EmployeeBatch.Status.REGISTERED) {
            throw new IllegalStateException("Hanya peserta REGISTERED yang bisa jadi ATTENDED");
        }
        if ((status == EmployeeBatch.Status.PASSED || status == EmployeeBatch.Status.FAILED)
                && current != EmployeeBatch.Status.ATTENDED) {
            throw new IllegalStateException("Peserta harus ATTENDED dulu sebelum PASSED/FAILED");
        }

        eb.setStatus(status);
        if (status == EmployeeBatch.Status.ATTENDED && eb.getAttendedAt() == null) {
            eb.setAttendedAt(LocalDate.now());
        }
        if ((status == EmployeeBatch.Status.PASSED || status == EmployeeBatch.Status.FAILED)
                && eb.getResultDate() == null) {
            eb.setResultDate(LocalDate.now());
        }

        if (score != null) eb.setScore(score);
        if (notes != null) eb.setNotes(notes);
        eb.setUpdatedAt(Instant.now());

        EmployeeBatch saved = repo.save(eb);

        // Kalau PASSED → auto create/update certification
        if (status == EmployeeBatch.Status.PASSED) {
            createOrUpdateCertification(saved);
        }

        return toResponse(saved);
    }

    private void createOrUpdateCertification(EmployeeBatch eb) {
        Employee emp = eb.getEmployee();
        CertificationRule rule = eb.getBatch().getCertificationRule();
        Institution institution = eb.getBatch().getInstitution();

        EmployeeCertification ec = certificationRepo
                .findFirstByEmployeeIdAndCertificationRuleIdAndDeletedAtIsNull(emp.getId(), rule.getId())
                .orElse(null);

        if (ec == null) {
            ec = EmployeeCertification.builder()
                    .employee(emp)
                    .certificationRule(rule)
                    .institution(institution)
                    .certDate(LocalDate.now())
                    .processType(EmployeeCertification.ProcessType.SERTIFIKASI)
                    .status(EmployeeCertification.Status.PENDING)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
        } else {
            ec.setCertDate(LocalDate.now());
            ec.setUpdatedAt(Instant.now());

            if (ec.getCertNumber() == null || ec.getCertNumber().isBlank()) {
                ec.setStatus(EmployeeCertification.Status.PENDING);
            } else {
                ec.setStatus(EmployeeCertification.Status.ACTIVE);
            }
        }

        if (ec.getCertDate() != null) {
            ec.setValidFrom(ec.getCertDate());

            if (rule != null && rule.getValidityMonths() != null) {
                ec.setValidUntil(ec.getCertDate().plusMonths(rule.getValidityMonths()));
            }
            if (rule != null && rule.getReminderMonths() != null && ec.getValidUntil() != null) {
                ec.setReminderDate(ec.getValidUntil().minusMonths(rule.getReminderMonths()));
            }
        }

        certificationRepo.save(ec);
    }

    // ================== SOFT DELETE ==================
    @Transactional
    public void removeParticipant(Long id) {
        EmployeeBatch eb = repo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("EmployeeBatch not found"));
        eb.setDeletedAt(Instant.now());
        eb.setUpdatedAt(Instant.now());
        repo.save(eb);
    }

    // ================== GET ELIGIBLE EMPLOYEES ==================
    @Transactional(readOnly = true)
    public List<EmployeeEligibilityResponse> getEligibleEmployeesForBatch(Long batchId) {
        Batch batch = batchRepo.findByIdAndDeletedAtIsNull(batchId)
                .orElseThrow(() -> new NotFoundException("Batch not found"));
        Long certRuleId = batch.getCertificationRule().getId();

        List<EmployeeEligibility> eligibles =
                eligibilityRepo.findByCertificationRule_IdAndIsActiveTrueAndDeletedAtIsNull(certRuleId);

        List<Long> existingIds = repo.findByBatch_IdAndDeletedAtIsNull(batchId)
                .stream()
                .map(eb -> eb.getEmployee().getId())
                .toList();

        return eligibles.stream()
                .filter(e -> e.getEmployee() != null && !existingIds.contains(e.getEmployee().getId()))
                .map(this::toEligibilityResponse)
                .toList();
    }
}
