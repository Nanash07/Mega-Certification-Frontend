package com.bankmega.certification.controller;

import com.bankmega.certification.dto.EmployeeExceptionRequest;
import com.bankmega.certification.dto.EmployeeExceptionResponse;
import com.bankmega.certification.dto.ExceptionImportResponse;
import com.bankmega.certification.entity.User;
import com.bankmega.certification.service.EmployeeExceptionService;
import com.bankmega.certification.service.EmployeeExceptionImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/exceptions")
@RequiredArgsConstructor
public class EmployeeExceptionController {

    private final EmployeeExceptionService exceptionService;
    private final EmployeeExceptionImportService importService;

    // ===================== CRUD =====================

    // 🔹 Get paged + filtered exceptions
    @GetMapping
    public ResponseEntity<Page<EmployeeExceptionResponse>> getPaged(
            @RequestParam(required = false) List<Long> jobIds,
            @RequestParam(required = false) List<String> certCodes,
            @RequestParam(required = false) List<Integer> levels,
            @RequestParam(required = false) List<String> subCodes,
            @RequestParam(required = false) String status, // ✅ tambah status
            @RequestParam(required = false) String search,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                exceptionService.getPagedFiltered(jobIds, certCodes, levels, subCodes, status, search, pageable)
        );
    }


    // 🔹 Get exceptions by employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeExceptionResponse>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(exceptionService.getByEmployee(employeeId));
    }

    // 🔹 Create new exception
    @PostMapping
    public ResponseEntity<EmployeeExceptionResponse> create(@RequestBody EmployeeExceptionRequest req) {
        return ResponseEntity.ok(
                exceptionService.create(req.getEmployeeId(), req.getCertificationRuleId(), req.getNotes())
        );
    }

    // 🔹 Update notes
    @PutMapping("/{id}/notes")
    public ResponseEntity<EmployeeExceptionResponse> updateNotes(
            @PathVariable Long id,
            @RequestParam String notes
    ) {
        return ResponseEntity.ok(exceptionService.updateNotes(id, notes));
    }

    // 🔹 Toggle aktif/nonaktif
    @PutMapping("/{id}/toggle")
    public ResponseEntity<EmployeeExceptionResponse> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(exceptionService.toggleActive(id));
    }

    // 🔹 Soft delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        exceptionService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    // ===================== IMPORT =====================

    // 🔹 Download template excel
    @GetMapping("/import/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        return importService.downloadTemplate();
    }

    // 🔹 Dry run import
    @PostMapping("/import/dry-run")
    public ResponseEntity<ExceptionImportResponse> dryRun(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user
    ) throws Exception {
        return ResponseEntity.ok(importService.dryRun(file, user));
    }

    // 🔹 Confirm import
    @PostMapping("/import/confirm")
    public ResponseEntity<ExceptionImportResponse> confirm(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user
    ) throws Exception {
        return ResponseEntity.ok(importService.confirm(file, user));
    }
}