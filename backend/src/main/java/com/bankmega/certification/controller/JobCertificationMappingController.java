package com.bankmega.certification.controller;

import com.bankmega.certification.dto.JobCertificationMappingRequest;
import com.bankmega.certification.dto.JobCertificationMappingResponse;
import com.bankmega.certification.service.JobCertificationMappingService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-certification-mappings")
@RequiredArgsConstructor
public class JobCertificationMappingController {

    private final JobCertificationMappingService service;

    @GetMapping("/paged")
    public ResponseEntity<Page<JobCertificationMappingResponse>> getPagedFiltered(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<Long> jobIds,
            @RequestParam(required = false) List<String> certCodes,
            @RequestParam(required = false) List<Integer> levels,
            @RequestParam(required = false) List<String> subCodes,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        return ResponseEntity.ok(service.getPagedFiltered(jobIds, certCodes, levels, subCodes, status, search, pageable));
    }

    @PostMapping
    public ResponseEntity<JobCertificationMappingResponse> create(@RequestBody JobCertificationMappingRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobCertificationMappingResponse> update(
            @PathVariable Long id, @RequestBody JobCertificationMappingRequest req
    ) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<JobCertificationMappingResponse> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggle(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}