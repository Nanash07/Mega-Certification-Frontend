package com.bankmega.certification.controller;

import com.bankmega.certification.dto.RegionalRequest;
import com.bankmega.certification.dto.RegionalResponse;
import com.bankmega.certification.service.RegionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regionals")
@RequiredArgsConstructor
public class RegionalController {

    private final RegionalService service;

    // ✅ Ambil semua (dropdown)
    @GetMapping("/all")
    public ResponseEntity<List<RegionalResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ✅ Search + Pagination
    @GetMapping
    public ResponseEntity<Page<RegionalResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.search(q, page, size));
    }

    // ✅ Get detail by ID
    @GetMapping("/{id}")
    public ResponseEntity<RegionalResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ✅ Create baru
    @PostMapping
    public ResponseEntity<RegionalResponse> create(@RequestBody RegionalRequest req) {
        return ResponseEntity.ok(service.createOrGet(req));
    }

    // ✅ Toggle aktif/nonaktif
    @PutMapping("/{id}/toggle")
    public ResponseEntity<RegionalResponse> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggle(id));
    }
}
