package com.bankmega.certification.service;

import com.bankmega.certification.dto.OrgResponse;
import com.bankmega.certification.entity.Regional;
import com.bankmega.certification.exception.ConflictException;
import com.bankmega.certification.exception.NotFoundException;
import com.bankmega.certification.repository.EmployeeRepository;
import com.bankmega.certification.repository.RegionalRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionalService {

    private final RegionalRepository repo;
    private final EmployeeRepository employeeRepo; // ✅ inject employeeRepo

    public List<OrgResponse> getAll() {
        return repo.findAllByOrderByIsActiveDescNameAsc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public OrgResponse createOrGet(String name) {
        Regional r = repo.findByNameIgnoreCase(name)
                .orElseGet(() -> repo.save(Regional.builder()
                        .name(name)
                        .isActive(true)
                        .build()));
        return mapToResponse(r);
    }

    public Page<OrgResponse> search(String q, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("isActive").descending().and(Sort.by("name").ascending()));

        Page<Regional> result;
        if (q == null || q.isBlank()) {
            result = repo.findAll(pageable);
        } else {
            result = repo.findByNameContainingIgnoreCase(q, pageable);
        }

        return result.map(this::mapToResponse);
    }

    @Transactional
    public OrgResponse toggle(Long id) {
        Regional r = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Regional not found: " + id));

        // 🔹 Cek dulu kalau mau dinonaktifkan
        if (r.getIsActive()) {
            boolean dipakai = employeeRepo.existsByRegional(r);
            if (dipakai) {
                throw new ConflictException("Regional masih dipakai oleh pegawai, tidak bisa dinonaktifkan");
            }
        }

        r.setIsActive(!r.getIsActive());
        return mapToResponse(repo.save(r));
    }

    private OrgResponse mapToResponse(Regional r) {
        return OrgResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .isActive(r.getIsActive())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
