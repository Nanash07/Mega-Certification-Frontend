package com.bankmega.certification.controller;

import com.bankmega.certification.dto.CertificationLevelRequest;
import com.bankmega.certification.dto.CertificationLevelResponse;
import com.bankmega.certification.service.CertificationLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certification-levels")
@RequiredArgsConstructor
public class CertificationLevelController {

    private final CertificationLevelService service;

    @GetMapping
    public ResponseEntity<List<CertificationLevelResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificationLevelResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<CertificationLevelResponse> create(@RequestBody CertificationLevelRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificationLevelResponse> update(@PathVariable Long id, @RequestBody CertificationLevelRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}