package com.bankmega.certification.controller;

import com.bankmega.certification.dto.RefreshmentTypeRequest;
import com.bankmega.certification.dto.RefreshmentTypeResponse;
import com.bankmega.certification.service.RefreshmentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/refreshment-types")
@RequiredArgsConstructor
public class RefreshmentTypeController {

    private final RefreshmentTypeService service;

    @GetMapping
    public List<RefreshmentTypeResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public RefreshmentTypeResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<RefreshmentTypeResponse> create(@RequestBody RefreshmentTypeRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RefreshmentTypeResponse> update(
            @PathVariable Long id,
            @RequestBody RefreshmentTypeRequest req
    ) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}