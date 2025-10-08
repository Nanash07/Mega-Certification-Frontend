package com.bankmega.certification.controller;

import com.bankmega.certification.dto.CertificationRequest;
import com.bankmega.certification.dto.CertificationResponse;
import com.bankmega.certification.service.CertificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService service;

    // 🔹 Get all certifications
    @GetMapping
    public ResponseEntity<List<CertificationResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 🔹 Get certification by ID
    @GetMapping("/{id}")
    public ResponseEntity<CertificationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // 🔹 Create new certification
    @PostMapping
    public ResponseEntity<CertificationResponse> create(@Valid @RequestBody CertificationRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    // 🔹 Update certification
    @PutMapping("/{id}")
    public ResponseEntity<CertificationResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CertificationRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    // 🔹 Soft delete certification
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
