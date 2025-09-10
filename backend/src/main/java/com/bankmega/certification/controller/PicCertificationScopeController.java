package com.bankmega.certification.controller;

import com.bankmega.certification.dto.PicCertificationScopeRequest;
import com.bankmega.certification.dto.PicCertificationScopeResponse;
import com.bankmega.certification.service.PicCertificationScopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pic-scope")
@RequiredArgsConstructor
public class PicCertificationScopeController {

    private final PicCertificationScopeService scopeService;

    // 🔹 Ambil semua PIC + scope-nya
    @GetMapping
    public ResponseEntity<List<PicCertificationScopeResponse>> getAll() {
        return ResponseEntity.ok(scopeService.getAll());
    }

    // 🔹 Ambil scope untuk 1 user PIC
    @GetMapping("/{userId}")
    public ResponseEntity<PicCertificationScopeResponse> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(scopeService.getByUser(userId));
    }

    // 🔹 Update scope PIC
    @PutMapping("/{userId}")
    public ResponseEntity<PicCertificationScopeResponse> updateScope(
            @PathVariable Long userId,
            @RequestBody PicCertificationScopeRequest request
    ) {
        return ResponseEntity.ok(scopeService.updateScope(userId, request));
    }
}