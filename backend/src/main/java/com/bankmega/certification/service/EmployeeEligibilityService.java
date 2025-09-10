package com.bankmega.certification.service;

import com.bankmega.certification.dto.EmployeeEligibilityResponse;
import com.bankmega.certification.entity.*;
import com.bankmega.certification.entity.EligibilitySource;
import com.bankmega.certification.entity.EligibilityStatus;
import com.bankmega.certification.repository.*;
import com.bankmega.certification.specification.EmployeeEligibilitySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmployeeEligibilityService {

    private final EmployeeEligibilityRepository eligibilityRepo;
    private final EmployeeCertificationRepository employeeCertificationRepo;
    private final JobCertificationMappingRepository jobCertMappingRepo;
    private final EmployeeCertificationExceptionRepository exceptionRepo;
    private final EmployeeRepository employeeRepo;

    // 🔹 Mapper entity → DTO
    private EmployeeEligibilityResponse toResponse(EmployeeEligibility e) {
        CertificationRule rule = e.getCertificationRule();
        Employee emp = e.getEmployee();

        // hitung tambahan field
        LocalDate wajibPunya = null;
        Integer masaBerlaku = null;
        String sisaWaktu = null;

        if (emp != null && emp.getJoinDate() != null && rule != null && rule.getWajibSetelahMasuk() != null) {
            wajibPunya = emp.getJoinDate().plusMonths(rule.getWajibSetelahMasuk());
        }
        if (rule != null) {
            masaBerlaku = rule.getValidityMonths();
        }
        if (e.getDueDate() != null) {
            Period period = Period.between(LocalDate.now(), e.getDueDate());
            if (!period.isNegative()) {
                sisaWaktu = period.getMonths() + " bulan " + period.getDays() + " hari";
            } else {
                sisaWaktu = "Kadaluarsa";
            }
        }

        return EmployeeEligibilityResponse.builder()
                .id(e.getId())
                .employeeId(emp != null ? emp.getId() : null)
                .employeeName(emp != null ? emp.getName() : null)
                .nip(emp != null ? emp.getNip() : null)
                .jobPositionTitle(emp != null && emp.getJobPosition() != null
                        ? emp.getJobPosition().getName()
                        : null
                )
                .joinDate(emp != null ? emp.getJoinDate() : null)

                .certificationRuleId(rule != null ? rule.getId() : null)
                .certificationCode(rule != null ? rule.getCertification().getCode() : null)
                .certificationName(rule != null ? rule.getCertification().getName() : null)

                .certificationLevelName(
                        rule != null && rule.getCertificationLevel() != null
                                ? rule.getCertificationLevel().getName()
                                : null
                )
                .certificationLevelLevel(
                        rule != null && rule.getCertificationLevel() != null
                                ? rule.getCertificationLevel().getLevel()
                                : null
                )
                .subFieldName(
                        rule != null && rule.getSubField() != null ? rule.getSubField().getName() : null
                )
                .subFieldCode(
                        rule != null && rule.getSubField() != null ? rule.getSubField().getCode() : null
                )

                .status(e.getStatus() != null ? e.getStatus().name() : null)
                .dueDate(e.getDueDate())
                .source(e.getSource() != null ? e.getSource().name() : null)
                .isActive(e.getIsActive())

                .wajibPunyaSertifikasiSampai(wajibPunya)
                .masaBerlakuBulan(masaBerlaku)
                .sisaWaktu(sisaWaktu)

                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    // 🔹 Paging + Filter + Search
    @Transactional(readOnly = true)
    public Page<EmployeeEligibilityResponse> getPagedFiltered(
            List<Long> jobIds,
            List<String> certCodes,
            List<Integer> levels,
            List<String> subCodes,
            List<String> statuses,
            String search,
            Pageable pageable
    ) {
        Specification<EmployeeEligibility> spec = EmployeeEligibilitySpecification.notDeleted()
                .and(EmployeeEligibilitySpecification.byJobIds(jobIds))
                .and(EmployeeEligibilitySpecification.byCertCodes(certCodes))
                .and(EmployeeEligibilitySpecification.byLevels(levels))
                .and(EmployeeEligibilitySpecification.bySubCodes(subCodes))
                .and(EmployeeEligibilitySpecification.byStatuses(statuses))
                .and(EmployeeEligibilitySpecification.bySearch(search));

        // 🚀 Default sort
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(
                            Sort.Order.asc("employee.jobPosition.name"),
                            Sort.Order.asc("certificationRule.certification.code"),
                            Sort.Order.asc("certificationRule.certificationLevel.level"),
                            Sort.Order.asc("certificationRule.subField.code"),
                            Sort.Order.asc("status")
                    )
            );
        }

        return eligibilityRepo.findAll(spec, pageable).map(this::toResponse);
    }

    // 🔹 Get detail
    @Transactional(readOnly = true)
    public EmployeeEligibilityResponse getById(Long id) {
        return eligibilityRepo.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Eligibility not found"));
    }

    // 🔹 Tambah eligibility manual (manual)
    @Transactional
    public EmployeeEligibilityResponse createFromManual(Long employeeId, CertificationRule rule) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeEligibility eligibility = eligibilityRepo
                .findByEmployeeAndCertificationRule(employee, rule)
                .orElseGet(EmployeeEligibility::new);

        eligibility.setEmployee(employee);
        eligibility.setCertificationRule(rule);
        eligibility.setSource(EligibilitySource.MANUAL);
        eligibility.setStatus(EligibilityStatus.BELUM_SERTIFIKASI);
        eligibility.setIsActive(true);

        eligibility.setValidityMonths(rule.getValidityMonths());
        eligibility.setReminderMonths(rule.getReminderMonths());
        eligibility.setWajibSetelahMasuk(rule.getWajibSetelahMasuk());

        return toResponse(eligibilityRepo.save(eligibility));
    }

    // 🔹 Toggle aktif/nonaktif
    @Transactional
    public EmployeeEligibilityResponse toggleActive(Long id) {
        EmployeeEligibility eligibility = eligibilityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Eligibility not found"));
        eligibility.setIsActive(!eligibility.getIsActive());
        return toResponse(eligibilityRepo.save(eligibility));
    }

    // 🔹 Soft delete
    @Transactional
    public void softDelete(Long id) {
        EmployeeEligibility eligibility = eligibilityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Eligibility not found"));
        eligibility.setDeletedAt(Instant.now());
        eligibilityRepo.save(eligibility);
    }

    // 🔹 Refresh Eligibility Engine (union mapping + manual)
    @Transactional
    public void refreshEligibility() {
        List<Employee> employees = employeeRepo.findAll();

        for (Employee employee : employees) {
            Long jobId = employee.getJobPosition() != null ? employee.getJobPosition().getId() : null;

            // mapping rules
            List<CertificationRule> mappingRules = jobId != null
                    ? jobCertMappingRepo.findByJobPosition_IdAndDeletedAtIsNull(jobId)
                            .stream()
                            .map(JobCertificationMapping::getCertificationRule)
                            .toList()
                    : List.of();

            // manual rules
            List<CertificationRule> manualRules = exceptionRepo.findByEmployeeIdAndDeletedAtIsNull(employee.getId())
                    .stream()
                    .map(EmployeeCertificationException::getCertificationRule)
                    .toList();

            // union
            Set<CertificationRule> allRules = new HashSet<>();
            allRules.addAll(mappingRules);
            allRules.addAll(manualRules);

            for (CertificationRule rule : allRules) {
                EmployeeEligibility eligibility = eligibilityRepo
                        .findByEmployeeAndCertificationRule(employee, rule)
                        .orElseGet(EmployeeEligibility::new);

                eligibility.setEmployee(employee);
                eligibility.setCertificationRule(rule);
                eligibility.setSource(determineSource(mappingRules, manualRules, rule));
                eligibility.setStatus(EligibilityStatus.BELUM_SERTIFIKASI);
                eligibility.setIsActive(true);

                eligibility.setValidityMonths(rule.getValidityMonths());
                eligibility.setReminderMonths(rule.getReminderMonths());
                eligibility.setWajibSetelahMasuk(rule.getWajibSetelahMasuk());

                eligibilityRepo.save(eligibility);
            }
        }

        syncWithCertifications();
    }

    private EligibilitySource determineSource(List<CertificationRule> mappingRules, List<CertificationRule> manualRules, CertificationRule rule) {
        boolean fromMapping = mappingRules.contains(rule);
        boolean fromManual = manualRules.contains(rule);

        if (fromMapping && fromManual) return EligibilitySource.MAPPING;
        if (fromMapping) return EligibilitySource.MAPPING;
        return EligibilitySource.MANUAL;
    }

    // 🔹 Sinkron eligibility dengan sertifikat
    private void syncWithCertifications() {
        List<EmployeeEligibility> eligibilities = eligibilityRepo.findAll();
        for (EmployeeEligibility ee : eligibilities) {
            Optional<EmployeeCertification> certOpt =
                    employeeCertificationRepo.findTopByEmployeeAndCertificationRuleAndDeletedAtIsNullOrderByCertDateDesc(
                            ee.getEmployee(), ee.getCertificationRule());

            if (certOpt.isPresent()) {
                EmployeeCertification cert = certOpt.get();
                Integer validity = ee.getValidityMonths();
                Integer reminder = ee.getReminderMonths();

                if (validity != null && cert.getCertDate() != null) {
                    LocalDate dueDate = cert.getCertDate().plusMonths(validity);
                    ee.setDueDate(dueDate);

                    if (LocalDate.now().isAfter(dueDate)) {
                        ee.setStatus(EligibilityStatus.EXPIRED);
                    } else if (reminder != null && LocalDate.now().isAfter(dueDate.minusMonths(reminder))) {
                        ee.setStatus(EligibilityStatus.DUE);
                    } else {
                        ee.setStatus(EligibilityStatus.AKTIF);
                    }
                } else {
                    ee.setStatus(EligibilityStatus.AKTIF); // sertifikat tanpa masa berlaku
                }
            } else {
                ee.setStatus(EligibilityStatus.BELUM_SERTIFIKASI);
            }

            eligibilityRepo.save(ee);
        }
    }
}