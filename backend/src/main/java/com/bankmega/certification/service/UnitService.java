package com.bankmega.certification.service;

import com.bankmega.certification.dto.OrgResponse;
import com.bankmega.certification.entity.Unit;
import com.bankmega.certification.exception.ConflictException;
import com.bankmega.certification.exception.NotFoundException;
import com.bankmega.certification.repository.EmployeeRepository;
import com.bankmega.certification.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository repo;
    private final EmployeeRepository employeeRepo;

    // ✅ Get all tanpa paging (dropdown dsb)
    public List<OrgResponse> getAll() {
        return repo.findAllByOrderByIsActiveDescNameAsc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ✅ Create baru atau ambil existing
    @Transactional
    public OrgResponse createOrGet(String name) {
        Unit u = repo.findByNameIgnoreCase(name)
                .orElseGet(() -> repo.save(Unit.builder()
                        .name(name)
                        .isActive(true)
                        .build()));
        return mapToResponse(u);
    }

    // ✅ Search + Pagination
    public Page<OrgResponse> search(String q, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("isActive").descending().and(Sort.by("name").ascending()));

        Page<Unit> result;
        if (q == null || q.isBlank()) {
            result = repo.findAll(pageable);
        } else {
            result = repo.findByNameContainingIgnoreCase(q, pageable);
        }

        return result.map(this::mapToResponse);
    }

    // ✅ Toggle aktif/nonaktif
    @Transactional
    public OrgResponse toggle(Long id) {
        Unit u = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Unit not found: " + id));

        if (Boolean.TRUE.equals(u.getIsActive())) {
            boolean dipakai = employeeRepo.existsByUnit(u);
            if (dipakai) {
                throw new ConflictException("Unit masih dipakai oleh pegawai, tidak bisa dinonaktifkan");
            }
        }

        u.setIsActive(!u.getIsActive());
        return mapToResponse(repo.save(u));
    }

    private OrgResponse mapToResponse(Unit u) {
        return OrgResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .isActive(u.getIsActive())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }
}