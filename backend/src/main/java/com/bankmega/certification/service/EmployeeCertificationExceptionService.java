package com.bankmega.certification.service;

import com.bankmega.certification.dto.EmployeeCertificationExceptionRequest;
import com.bankmega.certification.dto.EmployeeCertificationExceptionResponse;
import com.bankmega.certification.entity.CertificationRule;
import com.bankmega.certification.entity.Employee;
import com.bankmega.certification.entity.EmployeeCertificationException;
import com.bankmega.certification.repository.CertificationRuleRepository;
import com.bankmega.certification.repository.EmployeeCertificationExceptionRepository;
import com.bankmega.certification.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeCertificationExceptionService {

    private final EmployeeCertificationExceptionRepository exceptionRepository;
    private final EmployeeRepository employeeRepository;
    private final CertificationRuleRepository ruleRepository;

    @Transactional
    public EmployeeCertificationExceptionResponse create(EmployeeCertificationExceptionRequest req) {
        Employee employee = employeeRepository.findByIdAndDeletedAtIsNull(req.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Pegawai tidak ditemukan"));

        CertificationRule rule = ruleRepository.findById(req.getCertificationRuleId())
                .orElseThrow(() -> new IllegalArgumentException("Aturan sertifikasi tidak ditemukan"));

        EmployeeCertificationException exception = EmployeeCertificationException.builder()
                .employee(employee)
                .certificationRule(rule)
                .reason(req.getReason())
                .createdAt(Instant.now())
                .build();

        return toResponse(exceptionRepository.save(exception));
    }

    public List<EmployeeCertificationExceptionResponse> getAll() {
        return exceptionRepository.findAllByDeletedAtIsNull().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EmployeeCertificationExceptionResponse getById(Long id) {
        EmployeeCertificationException e = exceptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Data tidak ditemukan"));
        return toResponse(e);
    }

    @Transactional
    public EmployeeCertificationExceptionResponse update(Long id, EmployeeCertificationExceptionRequest req) {
        EmployeeCertificationException e = exceptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Data tidak ditemukan"));

        if (req.getEmployeeId() != null) {
            Employee employee = employeeRepository.findByIdAndDeletedAtIsNull(req.getEmployeeId())
                    .orElseThrow(() -> new IllegalArgumentException("Pegawai tidak ditemukan"));
            e.setEmployee(employee);
        }

        if (req.getCertificationRuleId() != null) {
            CertificationRule rule = ruleRepository.findById(req.getCertificationRuleId())
                    .orElseThrow(() -> new IllegalArgumentException("Aturan sertifikasi tidak ditemukan"));
            e.setCertificationRule(rule);
        }

        if (req.getReason() != null) {
            e.setReason(req.getReason());
        }

        e.setUpdatedAt(Instant.now());
        return toResponse(exceptionRepository.save(e));
    }

    @Transactional
    public void softDelete(Long id) {
        EmployeeCertificationException e = exceptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Data tidak ditemukan"));
        e.setDeletedAt(Instant.now());
        exceptionRepository.save(e);
    }

    private EmployeeCertificationExceptionResponse toResponse(EmployeeCertificationException e) {
        return EmployeeCertificationExceptionResponse.builder()
                .id(e.getId())
                .employeeId(e.getEmployee().getId())
                .nip(e.getEmployee().getNip())
                .employeeName(e.getEmployee().getName())
                .jobPositionTitle( // 🔥 ambil jabatan dari employee
                        e.getEmployee().getJobPosition() != null
                                ? e.getEmployee().getJobPosition().getName()
                                : "-"
                )
                .certificationRuleId(e.getCertificationRule().getId())
                .certificationCode(e.getCertificationRule().getCertification().getCode())
                .certificationLevel(
                        e.getCertificationRule().getCertificationLevel() != null
                                ? e.getCertificationRule().getCertificationLevel().getName()
                                : "-"
                )
                .subFieldCode(
                        e.getCertificationRule().getSubField() != null
                                ? e.getCertificationRule().getSubField().getCode()
                                : "-"
                )
                .reason(e.getReason())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .deletedAt(e.getDeletedAt())
                .build();
    }
}