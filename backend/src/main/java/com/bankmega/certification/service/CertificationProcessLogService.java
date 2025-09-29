package com.bankmega.certification.service;

import com.bankmega.certification.dto.CertificationProcessLogResponse;
import com.bankmega.certification.entity.CertificationProcessLog;
import com.bankmega.certification.entity.EmployeeCertification;
import com.bankmega.certification.repository.CertificationProcessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificationProcessLogService {

    private final CertificationProcessLogRepository logRepo;

    public void log(EmployeeCertification ec, CertificationProcessLog.ProcessType type, String notes) {
        CertificationProcessLog log = CertificationProcessLog.builder()
                .employeeCertification(ec)
                .processType(type)
                .processDate(LocalDate.now())
                .fileUrl(ec.getFileUrl())
                .notes(notes)
                .build();
        logRepo.save(log);
    }

    public List<CertificationProcessLogResponse> getLogs(Long certificationId) {
        return logRepo.findByEmployeeCertificationIdOrderByCreatedAtDesc(certificationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CertificationProcessLogResponse toResponse(CertificationProcessLog log) {
        return CertificationProcessLogResponse.builder()
                .id(log.getId())
                .employeeCertificationId(log.getEmployeeCertification().getId())
                .processType(log.getProcessType())
                .processDate(log.getProcessDate())
                .fileUrl(log.getFileUrl())
                .notes(log.getNotes())
                .createdAt(log.getCreatedAt())
                .updatedAt(log.getUpdatedAt())
                .build();
    }
}