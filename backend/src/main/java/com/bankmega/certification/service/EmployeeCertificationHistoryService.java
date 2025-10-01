package com.bankmega.certification.service;

import com.bankmega.certification.dto.EmployeeCertificationHistoryResponse;
import com.bankmega.certification.entity.EmployeeCertification;
import com.bankmega.certification.entity.EmployeeCertificationHistory;
import com.bankmega.certification.repository.EmployeeCertificationHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeCertificationHistoryService {

    private final EmployeeCertificationHistoryRepository historyRepo;
    private final ObjectMapper objectMapper;

    // ================== SNAPSHOT ==================
    public void snapshot(EmployeeCertification ec, EmployeeCertificationHistory.ActionType actionType) {
        try {
            // 🔹 Build snapshot minimalis (hindari lazy loading error)
            Map<String, Object> snapshotMap = new HashMap<>();
            snapshotMap.put("employeeId", ec.getEmployee().getId());
            snapshotMap.put("nip", ec.getEmployee().getNip());
            snapshotMap.put("employeeName", ec.getEmployee().getName());
            snapshotMap.put("certificationCode", ec.getCertificationRule().getCertification().getCode());
            snapshotMap.put("certNumber", ec.getCertNumber());
            snapshotMap.put("certDate", ec.getCertDate());
            snapshotMap.put("validUntil", ec.getValidUntil());
            snapshotMap.put("status", ec.getStatus());
            snapshotMap.put("fileUrl", ec.getFileUrl());

            String snapshotJson = objectMapper.writeValueAsString(snapshotMap);

            EmployeeCertificationHistory history = EmployeeCertificationHistory.builder()
                    .employeeCertification(ec)
                    .snapshot(snapshotJson)
                    .actionType(actionType)
                    .actionAt(Instant.now())
                    .build();

            historyRepo.save(history);
        } catch (Exception e) {
            throw new RuntimeException("Gagal menyimpan history sertifikat", e);
        }
    }

    // ================== GET HISTORY ==================
    public List<EmployeeCertificationHistoryResponse> getHistory(Long certificationId) {
        return historyRepo.findByEmployeeCertificationIdOrderByActionAtDesc(certificationId)
                .stream()
                .map(h -> EmployeeCertificationHistoryResponse.builder()
                        .id(h.getId())
                        .certificationId(h.getEmployeeCertification().getId())
                        .snapshot(h.getSnapshot())
                        .actionType(h.getActionType())
                        .actionAt(h.getActionAt())
                        .build())
                .collect(Collectors.toList());
    }
}
