package com.bankmega.certification.service;

import com.bankmega.certification.dto.RoleRequest;
import com.bankmega.certification.dto.RoleResponse;
import com.bankmega.certification.entity.Role;
import com.bankmega.certification.exception.BadRequestException;
import com.bankmega.certification.exception.ConflictException;
import com.bankmega.certification.exception.NotFoundException;
import com.bankmega.certification.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepo;

    // List semua role tanpa paging (dropdown dsb.)
    public List<RoleResponse> getAll() {
        return roleRepo.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream().map(this::toResp).toList();
    }

    // List role paging + search
    public Page<RoleResponse> getPage(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return roleRepo.findAll(pageable).map(this::toResp);
        }
        return roleRepo.findAll((root, cq, cb) -> {
            String like = "%" + q.trim().toUpperCase() + "%";
            return cb.like(cb.upper(root.get("name")), like);
        }, pageable).map(this::toResp);
    }

    public RoleResponse getById(Long id) {
        Role r = roleRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with id " + id + " not found"));
        return toResp(r);
    }

    @Transactional
    public RoleResponse create(RoleRequest dto) {
        String name = normalize(dto.getName());
        if (roleRepo.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Role name already exists: " + name);
        }
        Role saved = roleRepo.save(Role.builder().name(name).build());
        return toResp(saved);
    }

    @Transactional
    public RoleResponse update(Long id, RoleRequest dto) {
        Role r = roleRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with id " + id + " not found"));

        String name = normalize(dto.getName());
        if (!r.getName().equalsIgnoreCase(name) && roleRepo.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Role name already exists: " + name);
        }

        r.setName(name);
        return toResp(roleRepo.save(r));
    }

    @Transactional
    public void delete(Long id) {
        Role r = roleRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with id " + id + " not found"));

        long used = roleRepo.countUsersByRoleId(id);
        if (used > 0) {
            throw new BadRequestException("Role is currently assigned to " + used + " user(s)");
        }
        roleRepo.delete(r);
    }

    private String normalize(String s) {
        return s == null ? null : s.trim();
    }

    private RoleResponse toResp(Role r) {
        return RoleResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .build();
    }
}