package com.bankmega.certification.controller;

import com.bankmega.certification.dto.EmployeeCertificationExceptionRequest;
import com.bankmega.certification.dto.EmployeeCertificationExceptionResponse;
import com.bankmega.certification.service.EmployeeCertificationExceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-certification-exceptions")
@RequiredArgsConstructor
public class EmployeeCertificationExceptionController {

    private final EmployeeCertificationExceptionService service;

    @PostMapping
    public ResponseEntity<EmployeeCertificationExceptionResponse> create(
            @RequestBody EmployeeCertificationExceptionRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeCertificationExceptionResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeCertificationExceptionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeCertificationExceptionResponse> update(
            @PathVariable Long id,
            @RequestBody EmployeeCertificationExceptionRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}