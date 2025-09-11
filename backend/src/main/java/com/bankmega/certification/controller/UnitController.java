package com.bankmega.certification.controller;

import com.bankmega.certification.dto.UnitRequest;
import com.bankmega.certification.dto.UnitResponse;
import com.bankmega.certification.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService service;

    @GetMapping("/all")
    public ResponseEntity<List<UnitResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping
    public ResponseEntity<Page<UnitResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.search(q, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnitResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<UnitResponse> create(@RequestBody UnitRequest req) {
        return ResponseEntity.ok(service.createOrGet(req));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<UnitResponse> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggle(id));
    }
}