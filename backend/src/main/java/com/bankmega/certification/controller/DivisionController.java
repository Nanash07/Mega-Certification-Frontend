package com.bankmega.certification.controller;

import com.bankmega.certification.dto.OrgResponse;
import com.bankmega.certification.service.DivisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/divisions")
@RequiredArgsConstructor
public class DivisionController {

    private final DivisionService service;

    // ✅ Search + Pagination
    @GetMapping
    public ResponseEntity<Page<OrgResponse>> search(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.search(q, page, size));
    }

    // ✅ Create Division baru (?name=xxx)
    @PostMapping
    public ResponseEntity<OrgResponse> create(@RequestParam String name) {
        return ResponseEntity.ok(service.createOrGet(name));
    }

    // ✅ Toggle aktif/nonaktif
    @PutMapping("/{id}/toggle")
    public ResponseEntity<OrgResponse> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggle(id));
    }
}