package com.bankmega.certification.controller;

import com.bankmega.certification.dto.SubFieldRequest;
import com.bankmega.certification.dto.SubFieldResponse;
import com.bankmega.certification.service.SubFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subfields")
@RequiredArgsConstructor
public class SubFieldController {

    private final SubFieldService service;

    @GetMapping
    public ResponseEntity<List<SubFieldResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubFieldResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/certification/{certId}")
    public ResponseEntity<List<SubFieldResponse>> getByCertification(@PathVariable Long certId) {
        return ResponseEntity.ok(service.getByCertification(certId));
    }

    @PostMapping
    public ResponseEntity<SubFieldResponse> create(@RequestBody SubFieldRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubFieldResponse> update(@PathVariable Long id, @RequestBody SubFieldRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}