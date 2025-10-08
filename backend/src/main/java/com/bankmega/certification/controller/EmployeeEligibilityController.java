package com.bankmega.certification.controller;

import com.bankmega.certification.dto.EmployeeEligibilityResponse;
import com.bankmega.certification.service.EmployeeEligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee-eligibility")
@RequiredArgsConstructor
public class EmployeeEligibilityController {

    private final EmployeeEligibilityService service;

    // ===================== PAGED FILTERED =====================
    @GetMapping("/paged")
    public ResponseEntity<Page<EmployeeEligibilityResponse>> getPagedFiltered(
            @RequestParam(required = false) List<Long> employeeIds,
            @RequestParam(required = false) List<Long> jobIds,
            @RequestParam(required = false) List<String> certCodes,
            @RequestParam(required = false) List<Integer> levels,
            @RequestParam(required = false) List<String> subCodes,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) List<String> sources,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<EmployeeEligibilityResponse> result = service.getPagedFiltered(
                employeeIds, jobIds, certCodes, levels, subCodes, statuses, sources, search, pageable);
        return ResponseEntity.ok(result);
    }

    // ===================== GET BY EMPLOYEE (DETAIL PAGE) =====================
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeEligibilityResponse>> getByEmployee(@PathVariable Long employeeId) {
        // Optional: kalau mau auto-refresh data eligibility-nya sebelum return:
        // service.refreshEligibilityForEmployee(employeeId);

        List<EmployeeEligibilityResponse> data = service.getByEmployeeId(employeeId);
        return ResponseEntity.ok(data);
    }

    // ===================== GET BY ID =====================
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeEligibilityResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ===================== REFRESH MASS (SEMUA PEGAWAI) =====================
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshAll() {
        int count = service.refreshEligibility();
        return ResponseEntity.ok(Map.of(
                "message", "Eligibility refreshed for all employees",
                "refreshedCount", count));
    }

    // ===================== REFRESH PER EMPLOYEE =====================
    @PostMapping("/refresh/{employeeId}")
    public ResponseEntity<Map<String, Object>> refreshForEmployee(@PathVariable Long employeeId) {
        service.refreshEligibilityForEmployee(employeeId);
        return ResponseEntity.ok(Map.of(
                "message", "Eligibility refreshed for employee ID: " + employeeId));
    }

    // ===================== TOGGLE ACTIVE =====================
    @PutMapping("/{id}/toggle")
    public ResponseEntity<EmployeeEligibilityResponse> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggleActive(id));
    }

    // ===================== SOFT DELETE =====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> softDelete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.ok(Map.of("deletedId", id, "status", "success"));
    }
}
