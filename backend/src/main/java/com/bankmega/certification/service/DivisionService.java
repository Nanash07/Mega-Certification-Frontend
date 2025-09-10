package com.bankmega.certification.service;

import com.bankmega.certification.dto.OrgResponse;
import com.bankmega.certification.entity.Division;
import com.bankmega.certification.exception.ConflictException;
import com.bankmega.certification.exception.NotFoundException;
import com.bankmega.certification.repository.DivisionRepository;
import com.bankmega.certification.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DivisionService {

    private final DivisionRepository repo;
    private final EmployeeRepository employeeRepo;

    // 🔹 Ambil semua (buat dropdown)
    public List<OrgResponse> getAll() {
        return repo.findAllByOrderByIsActiveDescNameAsc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 🔹 Create atau ambil existing
    @Transactional
    public OrgResponse createOrGet(String name) {
        Division d = repo.findByNameIgnoreCase(name)
                .orElseGet(() -> repo.save(Division.builder()
                        .name(name)
                        .isActive(true)
                        .build()));
        return mapToResponse(d);
    }

    // 🔹 Search + Pagination
    public Page<OrgResponse> search(String q, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("isActive").descending().and(Sort.by("name").ascending()));

        Page<Division> result;
        if (q == null || q.isBlank()) {
            result = repo.findAll(pageable);
        } else {
            result = repo.findByNameContainingIgnoreCase(q, pageable);
        }

        return result.map(this::mapToResponse);
    }

    // 🔹 Toggle aktif/nonaktif
    @Transactional
    public OrgResponse toggle(Long id) {
        Division d = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Division not found: " + id));

        if (Boolean.TRUE.equals(d.getIsActive())) {
            boolean dipakai = employeeRepo.existsByDivision(d);
            if (dipakai) {
                throw new ConflictException("Division masih dipakai oleh pegawai, tidak bisa dinonaktifkan");
            }
        }

        d.setIsActive(!d.getIsActive());
        return mapToResponse(repo.save(d));
    }

    // 🔹 Mapper ke DTO
    private OrgResponse mapToResponse(Division d) {
        return OrgResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .isActive(d.getIsActive())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}