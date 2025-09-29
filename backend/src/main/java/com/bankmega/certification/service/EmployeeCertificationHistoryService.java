package com.bankmega.certification.service;

import com.bankmega.certification.dto.EmployeeCertificationHistoryResponse;
import com.bankmega.certification.entity.EmployeeCertification;
import com.bankmega.certification.entity.EmployeeCertificationHistory;
import com.bankmega.certification.repository.EmployeeCertificationHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeCertificationHistoryService {

    private final EmployeeCertificationHistoryRepository historyRepo;
    private final ObjectMapper objectMapper; // ✅ inject dari Spring

    public void saveHistory(EmployeeCertification ec, EmployeeCertificationHistory.ActionType actionType, String actionBy) {
        try {
            // simpan snapshot JSON dari entity
            String snapshot = objectMapper.writeValueAsString(ec);

            EmployeeCertificationHistory history = EmployeeCertificationHistory.builder()
                    .employeeCertification(ec)
                    .snapshot(snapshot)
                    .actionType(actionType)
                    .actionAt(Instant.now()) // bisa diganti audit otomatis
                    .actionBy(actionBy)
                    .build();

            historyRepo.save(history);
        } catch (Exception e) {
            throw new RuntimeException("Gagal menyimpan history sertifikat", e);
        }
    }

    public List<EmployeeCertificationHistoryResponse> getHistory(Long certificationId) {
        return historyRepo.findByEmployeeCertificationIdOrderByActionAtDesc(certificationId)
                .stream()
                .map(h -> EmployeeCertificationHistoryResponse.builder()
                        .id(h.getId())
                        .certificationId(h.getEmployeeCertification().getId())
                        .snapshot(h.getSnapshot())
                        .actionType(h.getActionType())
                        .actionAt(h.getActionAt())
                        .actionBy(h.getActionBy())
                        .build())
                .collect(Collectors.toList());
    }
}