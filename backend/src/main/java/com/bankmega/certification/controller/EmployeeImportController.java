package com.bankmega.certification.controller;

import com.bankmega.certification.dto.EmployeeImportLogResponse;
import com.bankmega.certification.dto.EmployeeImportResponse;
import com.bankmega.certification.entity.User;
import com.bankmega.certification.service.EmployeeImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/employees/import")
@RequiredArgsConstructor
public class EmployeeImportController {

    private final EmployeeImportService importService;

    // ✅ Dry Run (preview hasil import tanpa commit DB)
    @PostMapping("/dry-run")
    public ResponseEntity<EmployeeImportResponse> dryRun(
            @RequestParam("file") MultipartFile file,
            Principal principal) throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername(principal != null ? principal.getName() : "system");

        EmployeeImportResponse response = importService.dryRun(file, user);
        return ResponseEntity.ok(response);
    }

    // ✅ Confirm Import (commit ke DB + simpan log)
    @PostMapping("/confirm")
    public ResponseEntity<EmployeeImportResponse> confirm(
            @RequestParam("file") MultipartFile file,
            Principal principal) throws Exception {

        User user = new User();
        user.setId(1L);
        user.setUsername(principal != null ? principal.getName() : "system");

        EmployeeImportResponse response = importService.confirm(file, user);
        return ResponseEntity.ok(response);
    }

    // ✅ Download Template Excel
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        return importService.downloadTemplate();
    }

    // ✅ Ambil semua log import
    @GetMapping("/logs")
    public ResponseEntity<List<EmployeeImportLogResponse>> getAllLogs() {
        return ResponseEntity.ok(importService.getAllLogs());
    }

    // ✅ Ambil log import per user
    @GetMapping("/logs/{userId}")
    public ResponseEntity<List<EmployeeImportLogResponse>> getLogsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(importService.getLogsByUser(userId));
    }
}
