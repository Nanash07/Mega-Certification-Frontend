package com.bankmega.certification.controller;

import com.bankmega.certification.dto.EmployeeBatchResponse;
import com.bankmega.certification.dto.EmployeeEligibilityResponse;
import com.bankmega.certification.entity.EmployeeBatch;
import com.bankmega.certification.service.EmployeeBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

    // 🔹 Ambil peserta batch dengan paging (recommended FE pakai ini)
    @GetMapping("/batch/{batchId}/paged")
    public ResponseEntity<Page<EmployeeBatchResponse>> getPagedByBatch(
            @PathVariable Long batchId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EmployeeBatch.Status status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(service.search(batchId, search, status, pageable));
    }

    // 🔹 Search global (optional)
    @GetMapping
    public Page<EmployeeBatchResponse> search(
            @RequestParam(required = false) Long batchId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EmployeeBatch.Status status,
            Pageable pageable
    ) {
        return service.search(batchId, search, status, pageable);
    }

    // 🔹 Tambah peserta single
    @PostMapping("/batch/{batchId}/employee/{employeeId}")
    public EmployeeBatchResponse addParticipant(
            @PathVariable Long batchId,
            @PathVariable Long employeeId
    ) {
        return service.addParticipant(batchId, employeeId);
    }

    // 🔹 Tambah peserta bulk
    @PostMapping("/batch/{batchId}/employees/bulk")
    public ResponseEntity<List<EmployeeBatchResponse>> addParticipantsBulk(
            @PathVariable Long batchId,
            @RequestBody List<Long> employeeIds
    ) {
        return ResponseEntity.ok(service.addParticipantsBulk(batchId, employeeIds));
    }

    // 🔹 Update status peserta
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

    // 🔹 Eligible employees untuk batch
    @GetMapping("/batch/{batchId}/eligible")
    public List<EmployeeEligibilityResponse> getEligibleForBatch(@PathVariable Long batchId) {
        return service.getEligibleEmployeesForBatch(batchId);
    }
}