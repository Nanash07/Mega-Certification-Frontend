package com.bankmega.certification.controller;

import com.bankmega.certification.dto.EmployeeEligibilityResponse;
import com.bankmega.certification.entity.CertificationRule;
import com.bankmega.certification.service.EmployeeEligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-eligibility")
@RequiredArgsConstructor
public class EmployeeEligibilityController {

    private final EmployeeEligibilityService service;

    // 🔹 Ambil eligibility dengan paging + filter
    @GetMapping("/paged")
    public ResponseEntity<Page<EmployeeEligibilityResponse>> getPaged(
            @RequestParam(required = false) List<Long> jobIds,
            @RequestParam(required = false) List<String> certCodes,
            @RequestParam(required = false) List<Integer> levels,
            @RequestParam(required = false) List<String> subCodes,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                service.getPagedFiltered(jobIds, certCodes, levels, subCodes, statuses, search, pageable)
        );
    }

    // 🔹 Get detail by ID
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeEligibilityResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // 🔹 Tambah eligibility manual
    @PostMapping("/manual")
    public ResponseEntity<EmployeeEligibilityResponse> createManualEligibility(
            @RequestParam Long employeeId,
            @RequestBody CertificationRule rule
    ) {
        return ResponseEntity.ok(service.createFromManual(employeeId, rule));
    }

    // 🔹 Toggle aktif/nonaktif
    @PutMapping("/{id}/toggle")
    public ResponseEntity<EmployeeEligibilityResponse> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggleActive(id));
    }

    // 🔹 Soft delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Refresh eligibility (generate ulang)
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshEligibility() {
        service.refreshEligibility();
        return ResponseEntity.noContent().build();
    }
}