package com.bankmega.certification.service;

import com.bankmega.certification.dto.UserRequest;
import com.bankmega.certification.dto.UserResponse;
import com.bankmega.certification.entity.Employee;
import com.bankmega.certification.entity.Role;
import com.bankmega.certification.entity.User;
import com.bankmega.certification.exception.ConflictException;
import com.bankmega.certification.exception.NotFoundException;
import com.bankmega.certification.repository.EmployeeRepository;
import com.bankmega.certification.repository.RoleRepository;
import com.bankmega.certification.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final EmployeeRepository empRepo;

    // ✅ Paging + filter
    public Page<UserResponse> getPage(Long roleId, String q, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> cb.isNull(root.get("deletedAt"));

        if (roleId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("role").get("id"), roleId));
        }

        if (q != null && !q.isBlank()) {
            String like = "%" + q.toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("username")), like),
                            cb.like(cb.lower(root.get("email")), like)
                    ));
        }

        return userRepo.findAll(spec, pageable).map(this::mapToResponse);
    }

    public UserResponse getById(Long id) {
        return userRepo.findByIdAndDeletedAtIsNull(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    public UserResponse create(UserRequest req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new ConflictException("Username already used: " + req.getUsername());
        }
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new ConflictException("Email already used: " + req.getEmail());
        }

        Role role = roleRepo.findById(req.getRoleId())
                .orElseThrow(() -> new NotFoundException("Role not found with id " + req.getRoleId()));

        Employee emp = req.getEmployeeId() != null
                ? empRepo.findById(req.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee not found with id " + req.getEmployeeId()))
                : null;

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(BCrypt.hashpw(req.getPassword(), BCrypt.gensalt()))
                .role(role)
                .employee(emp)
                .isActive(req.getIsActive())
                .isFirstLogin(true)
                .build();

        return mapToResponse(userRepo.save(user));
    }

    public UserResponse update(Long id, UserRequest req) {
        User user = userRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));

        if (!user.getUsername().equals(req.getUsername()) &&
                userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new ConflictException("Username already used: " + req.getUsername());
        }
        if (!user.getEmail().equals(req.getEmail()) &&
                userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new ConflictException("Email already used: " + req.getEmail());
        }

        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(BCrypt.hashpw(req.getPassword(), BCrypt.gensalt()));
        }

        if (req.getRoleId() != null) {
            Role role = roleRepo.findById(req.getRoleId())
                    .orElseThrow(() -> new NotFoundException("Role not found with id " + req.getRoleId()));
            user.setRole(role);
        }

        if (req.getEmployeeId() != null) {
            Employee emp = empRepo.findById(req.getEmployeeId())
                    .orElseThrow(() -> new NotFoundException("Employee not found with id " + req.getEmployeeId()));
            user.setEmployee(emp);
        }

        user.setIsActive(req.getIsActive());

        return mapToResponse(userRepo.save(user));
    }

    public void softDelete(Long id) {
        User user = userRepo.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));
        user.setDeletedAt(Instant.now());
        userRepo.save(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleId(user.getRole() != null ? user.getRole().getId() : null)
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .employeeId(user.getEmployee() != null ? user.getEmployee().getId() : null)
                .isActive(user.getIsActive())
                .isFirstLogin(user.getIsFirstLogin())
                .build();
    }
}