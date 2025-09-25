package com.bankmega.certification.controller;

import com.bankmega.certification.dto.EmployeeBatchResponse;
import com.bankmega.certification.dto.EmployeeEligibilityResponse;
import com.bankmega.certification.entity.EmployeeBatch;
import com.bankmega.certification.service.EmployeeBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-batches")
@RequiredArgsConstructor
public class EmployeeBatchController {

    private final EmployeeBatchService service;

    // 🔹 Ambil semua peserta batch (tanpa paging)
    @GetMapping("/batch/{batchId}")
    public List<EmployeeBatchResponse> getByBatch(@PathVariable Long batchId) {
        return service.getByBatch(batchId);
    }

    // 🔹 Search + filter + paging
    @GetMapping
    public Page<EmployeeBatchResponse> search(
            @RequestParam(required = false) Long batchId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EmployeeBatch.Status status,
            Pageable pageable
    ) {
        return service.search(batchId, search, status, pageable);
    }

    // 🔹 Tambah peserta
    @PostMapping("/batch/{batchId}/employee/{employeeId}")
    public EmployeeBatchResponse addParticipant(
            @PathVariable Long batchId,
            @PathVariable Long employeeId
    ) {
        return service.addParticipant(batchId, employeeId);
    }

    // 🔹 Update status
    @PutMapping("/{id}/status")
    public EmployeeBatchResponse updateStatus(
            @PathVariable Long id,
            @RequestParam EmployeeBatch.Status status,
            @RequestParam(required = false) Integer score,
            @RequestParam(required = false) String notes
    ) {
        return service.updateStatus(id, status, score, notes);
    }

    // 🔹 Soft delete peserta
    @DeleteMapping("/{id}")
    public void removeParticipant(@PathVariable Long id) {
        service.removeParticipant(id);
    }

    // 🔹 Eligible employees untuk batch (berdasarkan tabel eligibility)
    @GetMapping("/batch/{batchId}/eligible")
    public List<EmployeeEligibilityResponse> getEligibleForBatch(@PathVariable Long batchId) {
        return service.getEligibleEmployeesForBatch(batchId);
    }
}
