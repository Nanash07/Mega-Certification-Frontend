package com.bankmega.certification.controller;

import com.bankmega.certification.dto.JobCertImportResponse;
import com.bankmega.certification.dto.JobCertImportLogResponse;
import com.bankmega.certification.entity.User;
import com.bankmega.certification.security.UserPrincipal;
import com.bankmega.certification.service.JobCertificationImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/job-certification-mappings/import")
@RequiredArgsConstructor
public class JobCertificationImportController {

    private final JobCertificationImportService importService;

    @PostMapping("/dry-run")
    public ResponseEntity<JobCertImportResponse> dryRun(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {

        User user = principal.getUser(); // 🔑 ambil entity
        return ResponseEntity.ok(importService.dryRun(file, user));
    }

    @PostMapping("/confirm")
    public ResponseEntity<JobCertImportResponse> confirm(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {

        User user = principal.getUser();
        return ResponseEntity.ok(importService.confirm(file, user));
    }

    @GetMapping("/logs")
    public ResponseEntity<List<JobCertImportLogResponse>> getLogs(
            @AuthenticationPrincipal User user) {
        if ("SUPERADMIN".equalsIgnoreCase(user.getRole().getName())) {
            return ResponseEntity.ok(importService.getAllLogsDto());
        } else {
            return ResponseEntity.ok(importService.getLogsByUserDto(user.getId()));
        }
    }

    @GetMapping("/template")
    public ResponseEntity<ByteArrayResource> downloadTemplate() {
        return importService.downloadTemplate();
    }
}