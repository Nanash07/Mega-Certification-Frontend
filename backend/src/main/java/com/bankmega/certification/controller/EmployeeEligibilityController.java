package com.bankmega.certification.controller;

import com.bankmega.certification.dto.EmployeeEligibilityResponse;
import com.bankmega.certification.service.EmployeeEligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee-eligibility")
@RequiredArgsConstructor
public class EmployeeEligibilityController {

    private final EmployeeEligibilityService service;

    @GetMapping("/paged")
    public Page<EmployeeEligibilityResponse> getPagedFiltered(
            @RequestParam(required = false) List<Long> employeeIds,
            @RequestParam(required = false) List<Long> jobIds,
            @RequestParam(required = false) List<String> certCodes,
            @RequestParam(required = false) List<Integer> levels,
            @RequestParam(required = false) List<String> subCodes,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) List<String> sources,
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        return service.getPagedFiltered(employeeIds, jobIds, certCodes, levels, subCodes, statuses, sources, search, pageable);
    }

    @PostMapping("/refresh")
    public Map<String, Object> refresh() {
        int count = service.refreshEligibility();
        return Map.of("refreshed", count);
    }

    @GetMapping("/{id}")
    public EmployeeEligibilityResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable Long id) {
        service.softDelete(id);
    }
}