package com.bankmega.certification.controller;

import com.bankmega.certification.dto.CertificationRuleRequest;
import com.bankmega.certification.dto.CertificationRuleResponse;
import com.bankmega.certification.service.CertificationRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certification-rules")
@RequiredArgsConstructor
public class CertificationRuleController {

    private final CertificationRuleService service;

    // 🔹 Paging + Filter + Search
    @GetMapping("/paged")
    public ResponseEntity<Page<CertificationRuleResponse>> getPagedFiltered(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<Long> certIds,
            @RequestParam(required = false) List<Long> levelIds,
            @RequestParam(required = false) List<Long> subIds,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        return ResponseEntity.ok(
                service.getPagedFiltered(certIds, levelIds, subIds, status, search, pageable));
    }

    // 🔹 All active rules (buat dropdown)
    @GetMapping("/all")
    public ResponseEntity<List<CertificationRuleResponse>> getAllActive() {
        return ResponseEntity.ok(service.getAllActive());
    }

    // 🔹 All rules (non-deleted)
    @GetMapping
    public ResponseEntity<List<CertificationRuleResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 🔹 Create
    @PostMapping
    public ResponseEntity<CertificationRuleResponse> create(@RequestBody CertificationRuleRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    // 🔹 Update
    @PutMapping("/{id}")
    public ResponseEntity<CertificationRuleResponse> update(
            @PathVariable Long id,
            @RequestBody CertificationRuleRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    // 🔹 Toggle aktif / nonaktif
    @PutMapping("/{id}/toggle")
    public ResponseEntity<CertificationRuleResponse> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggleStatus(id));
    }

    // 🔹 Soft delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}