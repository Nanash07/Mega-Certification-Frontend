package com.bankmega.certification.controller;

import com.bankmega.certification.dto.InstitutionRequest;
import com.bankmega.certification.dto.InstitutionResponse;
import com.bankmega.certification.service.InstitutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institutions")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

    @PostMapping
    public ResponseEntity<InstitutionResponse> create(@RequestBody InstitutionRequest req) {
        return ResponseEntity.ok(institutionService.create(req));
    }

    @GetMapping
    public ResponseEntity<List<InstitutionResponse>> getAll() {
        return ResponseEntity.ok(institutionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstitutionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(institutionService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstitutionResponse> update(
            @PathVariable Long id,
            @RequestBody InstitutionRequest req
    ) {
        return ResponseEntity.ok(institutionService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        institutionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}   