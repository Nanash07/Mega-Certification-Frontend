package com.bankmega.certification.controller;

import com.bankmega.certification.dto.DivisionRequest;
import com.bankmega.certification.dto.DivisionResponse;
import com.bankmega.certification.service.DivisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/divisions")
@RequiredArgsConstructor
public class DivisionController {

    private final DivisionService service;

    @GetMapping("/all")
    public ResponseEntity<List<DivisionResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping
    public ResponseEntity<Page<DivisionResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.search(q, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DivisionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<DivisionResponse> create(@RequestBody DivisionRequest req) {
        return ResponseEntity.ok(service.createOrGet(req));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<DivisionResponse> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggle(id));
    }
}