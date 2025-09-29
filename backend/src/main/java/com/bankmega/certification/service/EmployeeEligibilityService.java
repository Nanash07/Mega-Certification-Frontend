package com.bankmega.certification.service;

import com.bankmega.certification.dto.EmployeeEligibilityResponse;
import com.bankmega.certification.entity.*;
import com.bankmega.certification.repository.*;
import com.bankmega.certification.specification.EmployeeEligibilitySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeEligibilityService {

    private final EmployeeEligibilityRepository eligibilityRepo;
    private final EmployeeCertificationRepository employeeCertificationRepo;
    private final JobCertificationMappingRepository jobCertMappingRepo;
    private final EmployeeEligibilityExceptionRepository exceptionRepo;
    private final EmployeeRepository employeeRepo;

    // ===================== MAPPER =====================
    private EmployeeEligibilityResponse toResponse(EmployeeEligibility e) {
        CertificationRule rule = e.getCertificationRule();
        Employee emp = e.getEmployee();

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
            long days = ChronoUnit.DAYS.between(LocalDate.now(), e.getDueDate());
            sisaWaktu = days >= 0 ? days + " hari" : "Kadaluarsa";
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

    // ===================== PAGING + FILTER =====================
    @Transactional(readOnly = true)
    public Page<EmployeeEligibilityResponse> getPagedFiltered(
            List<Long> employeeIds,
            List<Long> jobIds,
            List<String> certCodes,
            List<Integer> levels,
            List<String> subCodes,
            List<String> statuses,
            List<String> sources,
            String search,
            Pageable pageable
    ) {
        Specification<EmployeeEligibility> spec = EmployeeEligibilitySpecification.notDeleted()
                .and(EmployeeEligibilitySpecification.byEmployeeIds(employeeIds))
                .and(EmployeeEligibilitySpecification.byJobIds(jobIds))
                .and(EmployeeEligibilitySpecification.byCertCodes(certCodes))
                .and(EmployeeEligibilitySpecification.byLevels(levels))
                .and(EmployeeEligibilitySpecification.bySubCodes(subCodes))
                .and(EmployeeEligibilitySpecification.byStatuses(statuses))
                .and(EmployeeEligibilitySpecification.bySources(sources));

        if (search != null && !search.isBlank()) {
            spec = spec.and(EmployeeEligibilitySpecification.bySearch(search));
        }

        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(
                            Sort.Order.asc("employee.nip"),
                            Sort.Order.asc("certificationRule.certification.code"),
                            Sort.Order.asc("certificationRule.certificationLevel.level"),
                            Sort.Order.asc("certificationRule.subField.code")
                    )
            );
        }

        Page<EmployeeEligibility> pageResult = eligibilityRepo.findAll(spec, pageable);

        pageResult.getContent().forEach(ee -> {
            ee.getEmployee().getName();
            ee.getCertificationRule().getCertification().getCode();
        });

        return pageResult.map(this::toResponse);
    }

    // ===================== GET DETAIL =====================
    @Transactional(readOnly = true)
    public EmployeeEligibilityResponse getById(Long id) {
        return eligibilityRepo.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Eligibility not found"));
    }

    // ===================== CREATE MANUAL =====================
    @Transactional
    public EmployeeEligibilityResponse createFromManual(Long employeeId, CertificationRule rule) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeEligibility eligibility = eligibilityRepo
                .findByEmployeeAndCertificationRuleAndSource(employee, rule, EmployeeEligibility.EligibilitySource.BY_NAME)
                .orElseGet(EmployeeEligibility::new);

        eligibility.setEmployee(employee);
        eligibility.setCertificationRule(rule);
        eligibility.setSource(EmployeeEligibility.EligibilitySource.BY_NAME);
        eligibility.setIsActive(true);
        eligibility.setDeletedAt(null);

        if (eligibility.getStatus() == null) {
            eligibility.setStatus(EmployeeEligibility.EligibilityStatus.NOT_YET_CERTIFIED);
        }

        eligibility.setValidityMonths(rule.getValidityMonths());
        eligibility.setReminderMonths(rule.getReminderMonths());
        eligibility.setWajibSetelahMasuk(rule.getWajibSetelahMasuk());

        return toResponse(eligibilityRepo.save(eligibility));
    }

    // ===================== TOGGLE ACTIVE =====================
    @Transactional
    public EmployeeEligibilityResponse toggleActive(Long id) {
        EmployeeEligibility eligibility = eligibilityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Eligibility not found"));
        eligibility.setIsActive(!eligibility.getIsActive());
        if (eligibility.getIsActive()) {
            eligibility.setDeletedAt(null);
        } else {
            eligibility.setDeletedAt(Instant.now());
        }
        return toResponse(eligibilityRepo.save(eligibility));
    }

    // ===================== SOFT DELETE =====================
    @Transactional
    public void softDelete(Long id) {
        EmployeeEligibility eligibility = eligibilityRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Eligibility not found"));
        eligibility.setIsActive(false);
        eligibility.setDeletedAt(Instant.now());
        eligibilityRepo.save(eligibility);
    }

    // ===================== REFRESH ENGINE (MASS) =====================
    @Transactional
    public int refreshEligibility() {
        List<Employee> employees = employeeRepo.findAll();

        Map<Long, List<CertificationRule>> jobRuleMap = jobCertMappingRepo.findAll().stream()
                .filter(j -> j.getDeletedAt() == null)
                .collect(Collectors.groupingBy(
                        j -> j.getJobPosition().getId(),
                        Collectors.mapping(JobCertificationMapping::getCertificationRule, Collectors.toList())
                ));

        Map<Long, List<CertificationRule>> exceptionRuleMap = exceptionRepo.findAll().stream()
                .filter(e -> e.getDeletedAt() == null && Boolean.TRUE.equals(e.getIsActive()))
                .collect(Collectors.groupingBy(
                        e -> e.getEmployee().getId(),
                        Collectors.mapping(EmployeeEligibilityException::getCertificationRule, Collectors.toList())
                ));

        List<EmployeeEligibility> allToSave = new ArrayList<>();

        for (Employee employee : employees) {
            allToSave.addAll(syncEligibilitiesForEmployee(employee, jobRuleMap, exceptionRuleMap));
        }

        eligibilityRepo.saveAll(allToSave);

        syncWithCertifications(employees);

        return allToSave.size();
    }

    // ===================== REFRESH ENGINE (PER EMPLOYEE) =====================
    @Transactional
    public void refreshEligibilityForEmployee(Long employeeId) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Map<Long, List<CertificationRule>> jobRuleMap = jobCertMappingRepo.findAll().stream()
                .filter(j -> j.getDeletedAt() == null)
                .collect(Collectors.groupingBy(
                        j -> j.getJobPosition().getId(),
                        Collectors.mapping(JobCertificationMapping::getCertificationRule, Collectors.toList())
                ));

        Map<Long, List<CertificationRule>> exceptionRuleMap = exceptionRepo.findAll().stream()
                .filter(e -> e.getDeletedAt() == null && Boolean.TRUE.equals(e.getIsActive()))
                .collect(Collectors.groupingBy(
                        e -> e.getEmployee().getId(),
                        Collectors.mapping(EmployeeEligibilityException::getCertificationRule, Collectors.toList())
                ));

        List<EmployeeEligibility> toSave = syncEligibilitiesForEmployee(employee, jobRuleMap, exceptionRuleMap);
        eligibilityRepo.saveAll(toSave);

        syncWithCertifications(List.of(employee));
    }

    // ===================== PRIVATE HELPER =====================
    private List<EmployeeEligibility> syncEligibilitiesForEmployee(
            Employee employee,
            Map<Long, List<CertificationRule>> jobRuleMap,
            Map<Long, List<CertificationRule>> exceptionRuleMap
    ) {
        Long jobId = employee.getJobPosition() != null ? employee.getJobPosition().getId() : null;

        List<CertificationRule> mappingRules = jobId != null
                ? jobRuleMap.getOrDefault(jobId, List.of())
                : List.of();

        List<CertificationRule> manualRules = exceptionRuleMap.getOrDefault(employee.getId(), List.of());

        List<EmployeeEligibility> existingElig = eligibilityRepo.findByEmployeeAndDeletedAtIsNull(employee);
        List<EmployeeEligibility> toSave = new ArrayList<>();

        // 🔹 Kumpulan semua rule yang harus dimiliki (gabungan job + exception)
        Set<Long> requiredRuleIds = new HashSet<>();
        mappingRules.forEach(r -> requiredRuleIds.add(r.getId()));
        manualRules.forEach(r -> requiredRuleIds.add(r.getId()));

        // 🔹 Nonaktifkan eligibility yang tidak relevan lagi
        for (EmployeeEligibility ee : existingElig) {
            if (!requiredRuleIds.contains(ee.getCertificationRule().getId())) {
                ee.setIsActive(false);
                ee.setDeletedAt(Instant.now());
                toSave.add(ee);
            }
        }

        // 🔹 Sinkronisasi eligibility per rule (prioritas EXCEPTION > JOB)
        for (Long ruleId : requiredRuleIds) {
            CertificationRule rule = null;
            // cari rule object dari mapping/exception
            for (CertificationRule r : manualRules) {
                if (r.getId().equals(ruleId)) {
                    rule = r;
                    break;
                }
            }
            if (rule == null) {
                for (CertificationRule r : mappingRules) {
                    if (r.getId().equals(ruleId)) {
                        rule = r;
                        break;
                    }
                }
            }

            if (rule == null) continue; // safety net

            // cek existing eligibility untuk employee + rule
            Optional<EmployeeEligibility> existingOpt = existingElig.stream()
                    .filter(ee -> ee.getCertificationRule().getId().equals(ruleId))
                    .findFirst();

            EmployeeEligibility eligibility = existingOpt.orElseGet(EmployeeEligibility::new);

            eligibility.setEmployee(employee);
            eligibility.setCertificationRule(rule);
            // 🚩 kalau ada exception untuk rule ini → BY_NAME, kalau nggak → BY_JOB
            if (manualRules.stream().anyMatch(r -> r.getId().equals(ruleId))) {
                eligibility.setSource(EmployeeEligibility.EligibilitySource.BY_NAME);
            } else {
                eligibility.setSource(EmployeeEligibility.EligibilitySource.BY_JOB);
            }

            if (eligibility.getStatus() == null) {
                eligibility.setStatus(EmployeeEligibility.EligibilityStatus.NOT_YET_CERTIFIED);
            }

            eligibility.setIsActive(true);
            eligibility.setDeletedAt(null);
            eligibility.setValidityMonths(rule.getValidityMonths());
            eligibility.setReminderMonths(rule.getReminderMonths());
            eligibility.setWajibSetelahMasuk(rule.getWajibSetelahMasuk());

            toSave.add(eligibility);
        }

        return toSave;
    }



    // ===================== SYNC WITH CERTIFICATIONS =====================
    private void syncWithCertifications(List<Employee> employees) {
        List<Long> employeeIds = employees.stream()
                .map(Employee::getId)
                .toList();

        List<EmployeeCertification> certs = employeeCertificationRepo.findByEmployeeIdInAndDeletedAtIsNull(employeeIds);

        // Ambil sertifikat terbaru per employee + rule
        Map<String, EmployeeCertification> latestCerts = certs.stream()
                .collect(Collectors.toMap(
                        c -> c.getEmployee().getId() + "-" + c.getCertificationRule().getId(),
                        c -> c,
                        (c1, c2) -> c1.getCertDate().isAfter(c2.getCertDate()) ? c1 : c2
                ));

        List<EmployeeEligibility> allEligibilities = eligibilityRepo.findByDeletedAtIsNull();
        for (EmployeeEligibility ee : allEligibilities) {
            String key = ee.getEmployee().getId() + "-" + ee.getCertificationRule().getId();
            EmployeeCertification cert = latestCerts.get(key);

            if (cert != null) {
                ee.setDueDate(cert.getValidUntil()); // sync langsung dari cert

                if (cert.getValidUntil() == null) {
                    ee.setStatus(EmployeeEligibility.EligibilityStatus.NOT_YET_CERTIFIED);
                } else if (LocalDate.now().isAfter(cert.getValidUntil())) {
                    ee.setStatus(EmployeeEligibility.EligibilityStatus.EXPIRED);
                } else if (cert.getReminderDate() != null && !LocalDate.now().isBefore(cert.getReminderDate())) {
                    ee.setStatus(EmployeeEligibility.EligibilityStatus.DUE);
                } else {
                    ee.setStatus(EmployeeEligibility.EligibilityStatus.ACTIVE);
                }
            } else {
                ee.setStatus(EmployeeEligibility.EligibilityStatus.NOT_YET_CERTIFIED);
                ee.setDueDate(null);
            }
        }

        eligibilityRepo.saveAll(allEligibilities);
    }
}