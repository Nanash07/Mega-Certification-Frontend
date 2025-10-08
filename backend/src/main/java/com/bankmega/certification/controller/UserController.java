package com.bankmega.certification.controller;

import com.bankmega.certification.dto.UserRequest;
import com.bankmega.certification.dto.UserResponse;
import com.bankmega.certification.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    // ===================== GET PAGE (with filter & search) =====================
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAll(
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<UserResponse> result = service.getPage(roleId, isActive, q, pageable);
        return ResponseEntity.ok(result);
    }

    // ===================== GET ACTIVE USERS (for dropdown / async select)
    // =====================
    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> getActiveUsers(
            @RequestParam(required = false) String q) {
        List<UserResponse> result = service.searchActiveUsers(q);
        return ResponseEntity.ok(result);
    }

    // ===================== GET ONE =====================
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ===================== CREATE =====================
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest req) {
        UserResponse created = service.create(req);
        return ResponseEntity
                .created(URI.create("/api/users/" + created.getId()))
                .body(created);
    }

    // ===================== UPDATE =====================
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    // ===================== TOGGLE STATUS (aktif / nonaktif) =====================
    @PutMapping("/{id}/toggle")
    public ResponseEntity<UserResponse> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggleStatus(id));
    }

    // ===================== DELETE (Soft Delete) =====================
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.ok(Map.of(
                "message", "User berhasil dihapus",
                "timestamp", System.currentTimeMillis()));
    }
}
